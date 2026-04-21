package com.nourane.shopflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse du panier avec calculs")
public class CartResponse {

    @Schema(description = "ID du panier", example = "1")
    private Long id;

    @Schema(description = "Liste des articles dans le panier")
    private List<CartItemResponse> items;

    @Schema(description = "Code promo appliqué", example = "BIENVENUE10")
    private String couponCode;

    @Schema(description = "Montant de la remise", example = "30.00")
    private BigDecimal couponRemise;

    @Schema(description = "Sous-total (hors livraison et remise)", example = "299.97")
    private BigDecimal sousTotal;

    @Schema(description = "Frais de livraison", example = "5.00")
    private BigDecimal livraisonFrais;

    @Schema(description = "Total TTC", example = "274.97")
    private BigDecimal total;

    @Schema(description = "Nombre total d'articles", example = "3")
    private Integer nombreArticles;
}