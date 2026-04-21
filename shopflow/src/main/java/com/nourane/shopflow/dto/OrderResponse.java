package com.nourane.shopflow.dto;

import com.nourane.shopflow.entity.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse complète d'une commande")
public class OrderResponse {

    @Schema(description = "ID de la commande", example = "1")
    private Long id;

    @Schema(description = "Numéro unique de commande", example = "ORD-2026-8F3D2A1E")
    private String numeroCommande;

    @Schema(description = "Date de création", example = "2026-01-15T10:30:00")
    private LocalDateTime dateCreation;

    @Schema(description = "Date de dernière modification", example = "2026-01-15T10:30:00")
    private LocalDateTime dateModification;

    @Schema(description = "Statut de la commande", example = "PENDING")
    private OrderStatus statut;

    @Schema(description = "Libellé du statut", example = "En attente de paiement")
    private String statutLibelle;

    @Schema(description = "Sous-total (hors livraison et remise)", example = "299.97")
    private BigDecimal sousTotal;

    @Schema(description = "Frais de livraison", example = "5.00")
    private BigDecimal livraisonFrais;

    @Schema(description = "Remise appliquée", example = "30.00")
    private BigDecimal remise;

    @Schema(description = "Total TTC", example = "274.97")
    private BigDecimal total;

    @Schema(description = "Adresse de livraison", example = "12 Avenue Habib Bourguiba")
    private String adresseLivraison;

    @Schema(description = "Ville de livraison", example = "Tunis")
    private String villeLivraison;

    @Schema(description = "Code postal", example = "1000")
    private String codePostalLivraison;

    @Schema(description = "Pays de livraison", example = "Tunisie")
    private String paysLivraison;

    @Schema(description = "Numéro de téléphone", example = "+216 99 999 999")
    private String telephoneLivraison;

    @Schema(description = "Notes supplémentaires", example = "Laisser le colis au concierge")
    private String notes;

    @Schema(description = "ID du client", example = "3")
    private Long customerId;

    @Schema(description = "Email du client", example = "client@shopflow.tn")
    private String customerEmail;

    @Schema(description = "Nom du client", example = "Mansour")
    private String customerNom;

    @Schema(description = "Prénom du client", example = "Amira")
    private String customerPrenom;

    @Schema(description = "Code promo utilisé", example = "BIENVENUE10")
    private String couponCode;

    @Schema(description = "Liste des articles commandés")
    private Set<OrderItemResponse> items;
}