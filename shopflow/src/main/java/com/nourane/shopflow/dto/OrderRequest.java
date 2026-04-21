package com.nourane.shopflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de création de commande")
public class OrderRequest {

    @NotBlank(message = "L'adresse de livraison est requise")
    @Size(min = 5, max = 255, message = "L'adresse doit contenir entre 5 et 255 caractères")
    @Schema(description = "Adresse de livraison", example = "12 Avenue Habib Bourguiba")
    private String adresseLivraison;

    @NotBlank(message = "La ville est requise")
    @Size(min = 2, max = 100, message = "La ville doit contenir entre 2 et 100 caractères")
    @Schema(description = "Ville de livraison", example = "Tunis")
    private String villeLivraison;

    @NotBlank(message = "Le code postal est requis")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "Le code postal doit contenir entre 4 et 10 chiffres")
    @Schema(description = "Code postal", example = "1000")
    private String codePostalLivraison;

    @NotBlank(message = "Le pays est requis")
    @Size(min = 2, max = 100, message = "Le pays doit contenir entre 2 et 100 caractères")
    @Schema(description = "Pays de livraison", example = "Tunisie")
    private String paysLivraison;

    @NotBlank(message = "Le numéro de téléphone est requis")
    @Pattern(regexp = "^[0-9+\\s]{8,20}$", message = "Format de téléphone invalide")
    @Schema(description = "Numéro de téléphone", example = "+216 99 999 999")
    private String telephoneLivraison;

    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    @Schema(description = "Notes supplémentaires", example = "Laisser le colis au concierge")
    private String notes;

    @Schema(description = "ID du coupon à appliquer (optionnel)", example = "BIENVENUE10")
    private String couponCode;
}