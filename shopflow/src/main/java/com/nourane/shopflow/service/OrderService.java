
package com.nourane.shopflow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nourane.shopflow.dto.cart.CartDTOs.CartResponse;
import com.nourane.shopflow.dto.order.OrderDTOs.*;
import com.nourane.shopflow.entity.*;
import com.nourane.shopflow.entity.enums.OrderStatus;
import com.nourane.shopflow.entity.enums.Role;
import com.nourane.shopflow.exception.BusinessException;
import com.nourane.shopflow.exception.ResourceNotFoundException;
import com.nourane.shopflow.exception.StockInsuffisantException;
import com.nourane.shopflow.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    public OrderResponse createOrder(CreateOrderRequest request, String email) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable", "email", email));

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new BusinessException("Panier introuvable"));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Le panier est vide");
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Adresse", request.getAddressId()));

        if (!address.getUser().getId().equals(customer.getId())) {
            throw new AccessDeniedException("Cette adresse ne vous appartient pas");
        }

        // Vérification finale du stock et décrément
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();

            if (!product.isActif()) {
                throw new BusinessException("Le produit '" + product.getNom() + "' n'est plus disponible");
            }

            int stockDispo = cartItem.getVariant() != null
                    ? cartItem.getVariant().getStockSupplementaire()
                    : product.getStock();

            if (stockDispo < cartItem.getQuantite()) {
                throw new StockInsuffisantException(product.getNom(), cartItem.getQuantite(), stockDispo);
            }

            // Décrémenter stock
            if (cartItem.getVariant() != null) {
                cartItem.getVariant().setStockSupplementaire(stockDispo - cartItem.getQuantite());
            } else {
                product.setStock(stockDispo - cartItem.getQuantite());
            }
            product.setTotalVentes(product.getTotalVentes() + cartItem.getQuantite());
            productRepository.save(product);

            BigDecimal prixUnit = product.isEnPromotion() ? product.getPrixPromo() : product.getPrix();
            if (cartItem.getVariant() != null) {
                prixUnit = prixUnit.add(cartItem.getVariant().getPrixDelta());
            }

            return OrderItem.builder()
                    .product(product)
                    .variant(cartItem.getVariant())
                    .quantite(cartItem.getQuantite())
                    .prixUnitaire(prixUnit)
                    .build();
        }).collect(Collectors.toList());

        // Calculer totaux depuis le panier
        CartResponse cartResponse = cartService.toResponse(cart);

        Order order = Order.builder()
                .customer(customer)
                .numeroCommande(generateNumeroCommande())
                .adresseLivraison(address)
                .sousTotal(cartResponse.getSousTotal())
                .fraisLivraison(cartResponse.getFraisLivraison())
                .remiseCoupon(cartResponse.getRemiseCoupon())
                .totalTTC(cartResponse.getTotalTTC())
                .coupon(cart.getCoupon())
                .statut(OrderStatus.PENDING)
                .build();

        order = orderRepository.save(order);

        // Lier les items à la commande
        final Order finalOrder = order;
        orderItems.forEach(item -> item.setOrder(finalOrder));
        order.setItems(orderItems);
        order = orderRepository.save(order);

        // Incrémenter usage coupon
        if (cart.getCoupon() != null) {
            Coupon coupon = cart.getCoupon();
            coupon.setUsagesActuels(coupon.getUsagesActuels() + 1);
            couponRepository.save(coupon);
        }

        // Vider le panier
        cart.getItems().clear();
        cart.setCoupon(null);
        cartRepository.save(cart);

        log.info("Commande créée : {} pour {}", order.getNumeroCommande(), email);
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable", "email", email));

        // Vérifier accès : client voit ses propres commandes, admin et vendeur voient tout
        if (user.getRole() == Role.CUSTOMER && !order.getCustomer().getEmail().equals(email)) {
            throw new AccessDeniedException("Accès refusé à cette commande");
        }

        // Marquer comme lue
        if (order.isNew()) {
            order.setNew(false);
            orderRepository.save(order);
        }

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(String email, int page, int size) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable", "email", email));
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCommande").descending());
        return orderRepository.findByCustomerId(customer.getId(), pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCommande").descending());
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getSellerOrders(String email, int page, int size) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable", "email", email));
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCommande").descending());
        return orderRepository.findBySellerId(seller.getId(), pageable).map(this::toResponse);
    }

    public OrderResponse updateStatus(Long id, UpdateStatusRequest request, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable", "email", email));

        // Validation de la transition de statut
        validateStatusTransition(order.getStatut(), request.getStatut(), user.getRole());

        order.setStatut(request.getStatut());
        order.setNew(true); // notifier le client
        orderRepository.save(order);

        log.info("Statut commande {} mis à jour : {}", order.getNumeroCommande(), request.getStatut());
        return toResponse(order);
    }

    public OrderResponse cancelOrder(Long id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable", "email", email));

        // Un client ne peut annuler que ses propres commandes
        if (user.getRole() == Role.CUSTOMER && !order.getCustomer().getEmail().equals(email)) {
            throw new AccessDeniedException("Accès refusé");
        }

        if (order.getStatut() != OrderStatus.PENDING && order.getStatut() != OrderStatus.PAID) {
            throw new BusinessException("Impossible d'annuler une commande avec le statut : " + order.getStatut());
        }

        order.setStatut(OrderStatus.CANCELLED);

        // Rembourser le stock
        order.getItems().forEach(item -> {
            if (item.getVariant() != null) {
                item.getVariant().setStockSupplementaire(
                        item.getVariant().getStockSupplementaire() + item.getQuantite());
            } else {
                item.getProduct().setStock(item.getProduct().getStock() + item.getQuantite());
                item.getProduct().setTotalVentes(item.getProduct().getTotalVentes() - item.getQuantite());
            }
            productRepository.save(item.getProduct());
        });

        if (order.getStatut() == OrderStatus.PAID) {
            order.setStatut(OrderStatus.REFUNDED);
        }

        orderRepository.save(order);
        log.info("Commande annulée : {}", order.getNumeroCommande());
        return toResponse(order);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void validateStatusTransition(OrderStatus actuel, OrderStatus nouveau, Role role) {
        if (role == Role.ADMIN) return; // L'admin peut tout faire
        boolean valide = switch (actuel) {
            case PENDING    -> nouveau == OrderStatus.PAID || nouveau == OrderStatus.CANCELLED;
            case PAID       -> nouveau == OrderStatus.PROCESSING || nouveau == OrderStatus.CANCELLED;
            case PROCESSING -> nouveau == OrderStatus.SHIPPED;
            case SHIPPED    -> nouveau == OrderStatus.DELIVERED;
            default         -> false;
        };
        if (!valide) {
            throw new BusinessException("Transition de statut invalide : " + actuel + " → " + nouveau);
        }
    }

    private String generateNumeroCommande() {
        String annee = String.valueOf(LocalDateTime.now().getYear());
        String random = String.format("%05d", new Random().nextInt(99999));
        return "ORD-" + annee + "-" + random;
    }

    public OrderResponse toResponse(Order order) {
        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setNumeroCommande(order.getNumeroCommande());
        r.setStatut(order.getStatut());
        r.setClientEmail(order.getCustomer().getEmail());
        r.setClientNom(order.getCustomer().getPrenom() + " " + order.getCustomer().getNom());
        r.setSousTotal(order.getSousTotal());
        r.setFraisLivraison(order.getFraisLivraison());
        r.setRemiseCoupon(order.getRemiseCoupon());
        r.setTotalTTC(order.getTotalTTC());
        r.setDateCommande(order.getDateCommande());
        r.setNew(order.isNew());
        r.setCouponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null);

        if (order.getAdresseLivraison() != null) {
            Address a = order.getAdresseLivraison();
            AddressSummary as = new AddressSummary();
            as.setId(a.getId());
            as.setRue(a.getRue());
            as.setVille(a.getVille());
            as.setCodePostal(a.getCodePostal());
            as.setPays(a.getPays());
            r.setAdresseLivraison(as);
        }

        r.setItems(order.getItems().stream().map(item -> {
            OrderItemResponse ir = new OrderItemResponse();
            ir.setId(item.getId());
            ir.setProductId(item.getProduct().getId());
            ir.setProductNom(item.getProduct().getNom());
            ir.setImageUrl(item.getProduct().getImages().isEmpty() ? null : item.getProduct().getImages().get(0));
            ir.setQuantite(item.getQuantite());
            ir.setPrixUnitaire(item.getPrixUnitaire());
            ir.setSousTotal(item.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantite())));
            if (item.getVariant() != null) {
                ir.setVariantInfo(item.getVariant().getAttribut() + " : " + item.getVariant().getValeur());
            }
            return ir;
        }).collect(Collectors.toList()));

        return r;
    }
}