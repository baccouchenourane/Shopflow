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
@Schema(description = "Article d'une commande")
public class OrderItemResponse {

    @Schema(description = "ID de l'article", example = "1")
    private Long id;

    @Schema(description = "ID du produit", example = "1")
    private Long productId;

    @Schema(description = "Nom du produit", example = "Sac Cuir Brun")
    private String productName;

    @Schema(description = "URL de l'image principale", example = "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600")
    private String productImage;

    @Schema(description = "ID de la variante (si applicable)", example = "5")
    private Long variantId;

    @Schema(description = "Nom de la variante", example = "Couleur Rouge")
    private String variantName;

    @Schema(description = "Quantité commandée", example = "2")
    private Integer quantity;

    @Schema(description = "Prix unitaire au moment de la commande", example = "149.99")
    private BigDecimal prixUnitaire;

    @Schema(description = "Total pour cet article", example = "299.98")
    private BigDecimal total;
}