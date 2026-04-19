package com.nourane.shopflow.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CartDTOs {

    @Data
    public static class AddItemRequest {
        @NotNull
        private Long productId;
        private Long variantId;
        @Min(1)
        private Integer quantite = 1;
    }

    @Data
    public static class UpdateItemRequest {
        @Min(1)
        @NotNull
        private Integer quantite;
    }

    @Data
    public static class CouponRequest {
        @NotNull
        private String code;
    }

    @Data
    public static class CartResponse {
        private Long id;
        private List<CartItemResponse> items;
        private BigDecimal sousTotal;
        private BigDecimal fraisLivraison;
        private BigDecimal remiseCoupon;
        private BigDecimal totalTTC;
        private String couponCode;
        private LocalDateTime dateModification;
    }

    @Data
    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String productNom;
        private String imageUrl;
        private Long variantId;
        private String variantInfo;
        private Integer quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal sousTotal;
        private Integer stockDisponible;
    }
}