package com.nourane.shopflow.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.nourane.shopflow.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTOs {

    @Data
    public static class CreateOrderRequest {
        @NotNull
        private Long addressId;
    }

    @Data
    public static class UpdateStatusRequest {
        @NotNull
        private OrderStatus statut;
    }

    @Data
    public static class OrderResponse {
        private Long id;
        private String numeroCommande;
        private OrderStatus statut;
        private String clientEmail;
        private String clientNom;
        private AddressSummary adresseLivraison;
        private List<OrderItemResponse> items;
        private BigDecimal sousTotal;
        private BigDecimal fraisLivraison;
        private BigDecimal remiseCoupon;
        private BigDecimal totalTTC;
        private String couponCode;
        private LocalDateTime dateCommande;
        private boolean isNew;
    }

    @Data
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productNom;
        private String imageUrl;
        private String variantInfo;
        private Integer quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal sousTotal;
    }

    @Data
    public static class AddressSummary {
        private Long id;
        private String rue;
        private String ville;
        private String codePostal;
        private String pays;
    }
}