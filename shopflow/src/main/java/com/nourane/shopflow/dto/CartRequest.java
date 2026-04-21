package com.nourane.shopflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour ajouter/modifier un article dans le panier")
public class CartRequest {

    @NotNull(message = "L'ID du produit est requis")
    @Schema(description = "ID du produit", example = "1")
    private Long productId;

    @Schema(description = "ID de la variante (optionnel)", example = "5")
    private Long variantId;

    @NotNull(message = "La quantité est requise")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    @Schema(description = "Quantité souhaitée", example = "2")
    private Integer quantity;
}