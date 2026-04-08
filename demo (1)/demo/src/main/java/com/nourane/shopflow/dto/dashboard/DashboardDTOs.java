package com.nourane.shopflow.dto.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTOs {

    @Data
    public static class AdminDashboard {
        private BigDecimal chiffreAffairesGlobal;
        private long totalCommandes;
        private long commandesEnAttente;
        private long totalUtilisateurs;
        private long totalProduits;
        private List<TopProduit> topProduits;
        private List<TopVendeur> topVendeurs;
        private List<CommandeRecente> commandesRecentes;
    }

    @Data
    public static class SellerDashboard {
        private BigDecimal revenus;
        private long commandesEnAttente;
        private long totalProduits;
        private List<StockFaible> alertesStockFaible;
        private List<CommandeRecente> commandesRecentes;
    }

    @Data
    public static class CustomerDashboard {
        private long commandesEnCours;
        private long totalCommandes;
        private List<CommandeRecente> mesCommandes;
    }

    @Data
    public static class TopProduit {
        private Long id;
        private String nom;
        private Integer totalVentes;
        private String imageUrl;
    }

    @Data
    public static class TopVendeur {
        private Long id;
        private String nomBoutique;
        private BigDecimal revenus;
    }

    @Data
    public static class CommandeRecente {
        private Long id;
        private String numeroCommande;
        private String statut;
        private String clientNom;
        private java.math.BigDecimal totalTTC;
        private java.time.LocalDateTime dateCommande;
    }

    @Data
    public static class StockFaible {
        private Long productId;
        private String nom;
        private Integer stock;
    }
}
