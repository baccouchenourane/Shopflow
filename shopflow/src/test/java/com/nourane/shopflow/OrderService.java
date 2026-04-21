package com.nourane.shopflow;


import com.nourane.shopflow.dto.CartItemResponse;
import com.nourane.shopflow.dto.CartRequest;
import com.nourane.shopflow.dto.CartResponse;
import com.nourane.shopflow.dto.CouponApplyRequest;
import com.nourane.shopflow.entity.*;
import com.nourane.shopflow.entity.enums.CouponType;
import com.nourane.shopflow.exception.BusinessException;
import com.nourane.shopflow.exception.ResourceNotFoundException;
import com.nourane.shopflow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final ProductVariantRepository productVariantRepository;

    private static final BigDecimal LIVRAISON_FRAIS = new BigDecimal("5.00");

    /**
     * Récupère le panier du client connecté
     */
    @Transactional(readOnly = true)
    public CartResponse getCart(String email) {
        User customer = getUserByEmail(email);
        Cart cart = getOrCreateCart(customer);
        return toResponse(cart);
    }

    /**
     * Ajoute un article au panier
     */
    @Transactional
    public CartResponse addItem(CartRequest request, String email) {
        User customer = getUserByEmail(email);
        Cart cart = getOrCreateCart(customer);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", request.getProductId()));

        // Vérifier si le produit est actif
        if (!product.isActif()) {
            throw new BusinessException("Ce produit n'est plus disponible");
        }

        // Vérifier la variante si spécifiée
        ProductVariant variant;
        if (request.getVariantId() != null) {
            variant = (ProductVariant) productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante", request.getVariantId()));

            // Vérifier que la variante appartient bien au produit
            if (!variant.getProduct().getId().equals(product.getId())) {
                throw new BusinessException("La variante ne correspond pas au produit");
            }

            // Vérifier le stock de la variante
            if (variant.getStock() < request.getQuantity()) {
                throw new BusinessException("Stock insuffisant pour cette variante. Stock disponible: " + variant.getStock());
            }
        } else {
            variant = null;
            // Vérifier le stock du produit
            if (product.getStock() < request.getQuantity()) {
                throw new BusinessException("Stock insuffisant. Stock disponible: " + product.getStock());
            }
        }

        // Vérifier si l'article existe déjà dans le panier
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()) &&
                        ((variant == null && item.getVariant() == null) ||
                                (variant != null && item.getVariant() != null && item.getVariant().getId().equals(variant.getId()))))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Mettre à jour la quantité
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            // Vérifier le stock total
            if (variant != null) {
                if (variant.getStock() < newQuantity) {
                    throw new BusinessException("Stock total insuffisant. Stock disponible: " + variant.getStock());
                }
            } else {
                if (product.getStock() < newQuantity) {
                    throw new BusinessException("Stock total insuffisant. Stock disponible: " + product.getStock());
                }
            }

            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            // Créer un nouvel article
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)
                    .quantite(request.getQuantity())
                    .prixUnitaire(getProductPrice(product, variant))
                    .build();
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cart.setDateModification(LocalDate.now().atStartOfDay());
        cartRepository.save(cart);

        log.info("Article ajouté au panier de {}: produit {} x{}", email, product.getNom(), request.getQuantity());

        return toResponse(cart);
    }

    /**
     * Modifie la quantité d'un article dans le panier
     */
    @Transactional
    public CartResponse updateItem(Long itemId, CartRequest request, String email) {
        User customer = getUserByEmail(email);
        Cart cart = getOrCreateCart(customer);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Article du panier", itemId));

        // Vérifier que l'article appartient au panier de l'utilisateur
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Cet article n'appartient pas à votre panier");
        }

        if (request.getQuantity() <= 0) {
            // Supprimer l'article si quantité = 0
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            // Vérifier le stock
            if (item.getVariant() != null) {
                if (item.getVariant().getStock() < request.getQuantity()) {
                    throw new BusinessException("Stock insuffisant pour cette variante. Stock disponible: " + item.getVariant().getStock());
                }
            } else {
                if (item.getProduct().getStock() < request.getQuantity()) {
                    throw new BusinessException("Stock insuffisant. Stock disponible: " + item.getProduct().getStock());
                }
            }
            item.setQuantity(request.getQuantity());
            cartItemRepository.save(item);
        }

        cart.setDateModification(LocalDate.now().atStartOfDay());
        cartRepository.save(cart);

        return toResponse(cart);
    }

    /**
     * Supprime un article du panier
     */
    @Transactional
    public CartResponse removeItem(Long itemId, String email) {
        User customer = getUserByEmail(email);
        Cart cart = getOrCreateCart(customer);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Article du panier", itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Cet article n'appartient pas à votre panier");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        cart.setDateModification(LocalDate.now().atStartOfDay());
        cartRepository.save(cart);

        return toResponse(cart);
    }

    /**
     * Applique un coupon au panier
     */
    @Transactional
    public CartResponse applyCoupon(CouponApplyRequest request, String email) {
        User customer = getUserByEmail(email);
        Cart cart = getOrCreateCart(customer);

        Coupon coupon = (Coupon) couponRepository.findByCodeAndActifTrue(request.getCode())
                .orElseThrow(() -> new BusinessException("Code promo invalide ou expiré"));

        // Vérifier la date d'expiration
        if (coupon.getDateExpiration().isBefore(LocalDate.now().atStartOfDay())) {
            throw new BusinessException("Ce code promo a expiré");
        }

        // Vérifier le nombre d'utilisations
        if (coupon.getUsagesActuels() >= coupon.getUsagesMax()) {
            throw new BusinessException("Ce code promo a atteint sa limite d'utilisations");
        }

        cart.setCoupon(coupon);
        cartRepository.save(cart);

        log.info("Coupon {} appliqué au panier de {}", request.getCode(), email);

        return toResponse(cart);
    }

    /**
     * Retire le coupon du panier
     */
    @Transactional
    public CartResponse removeCoupon(String email) {
        User customer = getUserByEmail(email);
        Cart cart = getOrCreateCart(customer);

        cart.setCoupon(null);
        cartRepository.save(cart);

        return toResponse(cart);
    }

    /**
     * Vide complètement le panier
     */
    @Transactional
    public void clearCart(String email) {
        User customer = getUserByEmail(email);
        Cart cart = getOrCreateCart(customer);

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setCoupon(null);
        cartRepository.save(cart);
    }

    /**
     * Helper: Récupère ou crée un panier pour un client
     */
    private Cart getOrCreateCart(User customer) {
        return cartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .customer(customer)
                            .items(new ArrayList<>())
                            .dateModification(LocalDate.now().atStartOfDay())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Helper: Récupère l'utilisateur par son email
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    /**
     * Helper: Calcule le prix d'un produit (avec ou sans promo, avec ou sans variante)
     */
    private BigDecimal getProductPrice(Product product, ProductVariant variant) {
        if (variant != null && variant.getPrixSupplement() != null) {
            BigDecimal basePrice = product.getPrixPromo() != null ? product.getPrixPromo() : product.getPrix();
            return basePrice.add(variant.getPrixSupplement());
        }
        return product.getPrixPromo() != null ? product.getPrixPromo() : product.getPrix();
    }

    /**
     * Convertit un panier en DTO de réponse avec tous les calculs
     */
    public CartResponse toResponse(Cart cart) {
        // Calcul du sous-total
        BigDecimal sousTotal = cart.getItems().stream()
                .map(item -> item.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcul de la remise coupon
        BigDecimal remise = BigDecimal.ZERO;
        if (cart.getCoupon() != null) {
            Coupon coupon = cart.getCoupon();
            if (coupon.getType() == CouponType.PERCENT) {
                remise = sousTotal.multiply(coupon.getValeur().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            } else if (coupon.getType() == CouponType.FIXED) {
                remise = coupon.getValeur().min(sousTotal); // Ne pas dépasser le sous-total
            }
        }

        BigDecimal apresRemise = sousTotal.subtract(remise);
        BigDecimal total = apresRemise.add(LIVRAISON_FRAIS);

        return CartResponse.builder()
                .id(cart.getId())
                .items(cart.getItems().stream()
                        .map(this::toCartItemResponse)
                        .collect(Collectors.toList()))
                .couponCode(cart.getCoupon() != null ? cart.getCoupon().getCode() : null)
                .couponRemise(remise)
                .sousTotal(sousTotal)
                .livraisonFrais(LIVRAISON_FRAIS)
                .total(total)
                .nombreArticles(cart.getItems().stream().mapToInt(CartItem::getQuantity).sum())
                .build();
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getNom())
                .productImage(item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()
                        ? item.getProduct().getImages().get(0) : null)
                .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                .variantName(item.getVariant() != null ? item.getVariant().getNom() : null)
                .prixUnitaire(item.getPrixUnitaire())
                .quantity(item.getQuantity())
                .total(item.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}