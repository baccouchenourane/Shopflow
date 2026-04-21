package com.nourane.shopflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article dans le panier")
public class CartItemResponse {

    @Schema(description = "ID de l'article dans le panier", example = "1")
    private Long id;

    @Schema(description = "ID du produit", example = "1")
    private Long productId;

    @Schema(description = "Nom du produit", example = "Sac Cuir Brun")
    private String productName;

    @Schema(description = "URL de l'image", example = "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600")
    private String productImage;

    @Schema(description = "ID de la variante", example = "5")
    private Long variantId;

    @Schema(description = "Nom de la variante", example = "Couleur Rouge")
    private String variantName;

    @Schema(description = "Prix unitaire", example = "149.99")
    private BigDecimal prixUnitaire;

    @Schema(description = "Quantité", example = "2")
    private Integer quantity;

    @Schema(description = "Total pour cet article", example = "299.98")
    private BigDecimal total;
}