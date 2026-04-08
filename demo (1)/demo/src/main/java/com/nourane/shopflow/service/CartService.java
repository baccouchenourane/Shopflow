

package com.nourane.shopflow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nourane.shopflow.dto.cart.CartDTOs.*;
import com.nourane.shopflow.entity.*;
import com.nourane.shopflow.exception.BusinessException;
import com.nourane.shopflow.exception.ResourceNotFoundException;
import com.nourane.shopflow.exception.StockInsuffisantException;
import com.nourane.shopflow.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    private static final BigDecimal FRAIS_LIVRAISON = BigDecimal.valueOf(5.0);

    @Transactional(readOnly = true)
    public CartResponse getCart(String email) {
        Cart cart = getOrCreateCart(email);
        return toResponse(cart);
    }

    public CartResponse addItem(AddItemRequest request, String email) {
        Cart cart = getOrCreateCart(email);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", request.getProductId()));

        if (!product.isActif()) {
            throw new BusinessException("Ce produit n'est plus disponible");
        }

        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(request.getVariantId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Variante introuvable"));
        }

        // Vérifier stock
        int stockDispo = variant != null ? variant.getStockSupplementaire() : product.getStock();
        if (stockDispo < request.getQuantite()) {
            throw new StockInsuffisantException(product.getNom(), request.getQuantite(), stockDispo);
        }

        // Si l'article existe déjà dans le panier, mettre à jour la quantité
        final ProductVariant finalVariant = variant;
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId())
                        && (finalVariant == null ? item.getVariant() == null
                            : finalVariant.getId().equals(item.getVariant() != null ? item.getVariant().getId() : null)))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQty = existingItem.getQuantite() + request.getQuantite();
            if (stockDispo < newQty) {
                throw new StockInsuffisantException(product.getNom(), newQty, stockDispo);
            }
            existingItem.setQuantite(newQty);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)
                    .quantite(request.getQuantite())
                    .build();
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
        return toResponse(cart);
    }

    public CartResponse updateItem(Long itemId, UpdateItemRequest request, String email) {
        Cart cart = getOrCreateCart(email);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable dans le panier"));

        int stockDispo = item.getVariant() != null
                ? item.getVariant().getStockSupplementaire()
                : item.getProduct().getStock();

        if (stockDispo < request.getQuantite()) {
            throw new StockInsuffisantException(item.getProduct().getNom(), request.getQuantite(), stockDispo);
        }

        item.setQuantite(request.getQuantite());
        cartRepository.save(cart);
        return toResponse(cart);
    }

    public CartResponse removeItem(Long itemId, String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        cartRepository.save(cart);
        return toResponse(cart);
    }

    public CartResponse applyCoupon(CouponRequest request, String email) {
        Cart cart = getOrCreateCart(email);

        Coupon coupon = couponRepository.findByCode(request.getCode())
                .orElseThrow(() -> new BusinessException("Code promo invalide"));

        if (!coupon.isValide()) {
            throw new BusinessException("Ce coupon est expiré ou a atteint sa limite d'utilisation");
        }

        cart.setCoupon(coupon);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    public CartResponse removeCoupon(String email) {
        Cart cart = getOrCreateCart(email);
        cart.setCoupon(null);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    public Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return cartRepository.findByCustomerId(user.getId())
                .orElseGet(() -> {
                    Cart cart = Cart.builder().customer(user).build();
                    return cartRepository.save(cart);
                });
    }

    public CartResponse toResponse(Cart cart) {
        CartResponse r = new CartResponse();
        r.setId(cart.getId());
        r.setDateModification(cart.getDateModification());
        r.setCouponCode(cart.getCoupon() != null ? cart.getCoupon().getCode() : null);

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
        r.setItems(itemResponses);

        BigDecimal sousTotal = itemResponses.stream()
                .map(CartItemResponse::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        r.setSousTotal(sousTotal);
        r.setFraisLivraison(cart.getItems().isEmpty() ? BigDecimal.ZERO : FRAIS_LIVRAISON);

        // Calcul remise coupon
        BigDecimal remise = BigDecimal.ZERO;
        if (cart.getCoupon() != null && cart.getCoupon().isValide()) {
            Coupon coupon = cart.getCoupon();
            remise = switch (coupon.getType()) {
                case PERCENT -> sousTotal.multiply(coupon.getValeur())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                case FIXED   -> coupon.getValeur().min(sousTotal);
            };
        }
        r.setRemiseCoupon(remise);
        r.setTotalTTC(sousTotal.add(r.getFraisLivraison()).subtract(remise).max(BigDecimal.ZERO));
        return r;
    }

    private CartItemResponse toItemResponse(CartItem item) {
        CartItemResponse r = new CartItemResponse();
        r.setId(item.getId());
        r.setProductId(item.getProduct().getId());
        r.setProductNom(item.getProduct().getNom());
        r.setImageUrl(item.getProduct().getImages().isEmpty() ? null : item.getProduct().getImages().get(0));
        r.setQuantite(item.getQuantite());

        BigDecimal prixBase = item.getProduct().isEnPromotion()
                ? item.getProduct().getPrixPromo()
                : item.getProduct().getPrix();

        if (item.getVariant() != null) {
            r.setVariantId(item.getVariant().getId());
            r.setVariantInfo(item.getVariant().getAttribut() + " : " + item.getVariant().getValeur());
            prixBase = prixBase.add(item.getVariant().getPrixDelta());
            r.setStockDisponible(item.getVariant().getStockSupplementaire());
        } else {
            r.setStockDisponible(item.getProduct().getStock());
        }

        r.setPrixUnitaire(prixBase);
        r.setSousTotal(prixBase.multiply(BigDecimal.valueOf(item.getQuantite())));
        return r;
    }
} 
