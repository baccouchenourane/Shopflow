package com.nourane.shopflow.dto;

import com.nourane.shopflow.entity.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de mise à jour du statut d'une commande")
public class OrderStatusUpdateRequest {

    @NotNull(message = "Le nouveau statut est requis")
    @Schema(description = "Nouveau statut de la commande",
            example = "PROCESSING",
            allowableValues = {"PENDING", "PAID", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"})
    private OrderStatus newStatus;

    @Schema(description = "Commentaire optionnel sur le changement de statut",
            example = "Préparation en cours")
    private String commentaire;
}