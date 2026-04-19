package com.nourane.shopflow.dto.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDTOs {

    @Data
    public static class ProductRequest {
        @NotBlank
        private String nom;

        private String description;

        @NotNull @DecimalMin("0.01")
        private BigDecimal prix;

        private BigDecimal prixPromo;

        @NotNull @Min(0)
        private Integer stock;

        private List<Long> categoryIds;
        private List<String> images;
        private List<VariantRequest> variants;
    }

    @Data
    public static class VariantRequest {
        @NotBlank
        private String attribut;
        @NotBlank
        private String valeur;
        @Min(0)
        private Integer stockSupplementaire = 0;
        private BigDecimal prixDelta = BigDecimal.ZERO;
    }

    @Data
    public static class ProductResponse {
        private Long id;
        private String nom;
        private String description;
        private BigDecimal prix;
        private BigDecimal prixPromo;
        private boolean enPromotion;
        private Double pourcentageRemise;
        private Integer stock;
        private boolean actif;
        private LocalDateTime dateCreation;
        private Integer totalVentes;
        private List<String> images;
        private List<CategorySummary> categories;
        private List<VariantResponse> variants;
        private Double noteMoyenne;
        private Long sellerId;
        private String nomBoutique;
    }

    @Data
    public static class VariantResponse {
        private Long id;
        private String attribut;
        private String valeur;
        private Integer stockSupplementaire;
        private BigDecimal prixDelta;
    }

    @Data
    public static class CategorySummary {
        private Long id;
        private String nom;
    }

    @Data
    public static class ProductSummary {
        private Long id;
        private String nom;
        private BigDecimal prix;
        private BigDecimal prixPromo;
        private boolean enPromotion;
        private String imageUrl;
        private Double noteMoyenne;
        private Integer stock;
    }
}
