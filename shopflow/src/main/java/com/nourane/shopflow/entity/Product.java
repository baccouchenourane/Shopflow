package com.nourane.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"categories", "variants", "reviews"})
@ToString(exclude = {"categories", "variants", "reviews"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(precision = 10, scale = 2)
    private BigDecimal prixPromo;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Builder.Default
    private boolean actif = true;

    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Builder.Default
    private Integer totalVentes = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    public boolean isEnPromotion() {
        return prixPromo != null && prixPromo.compareTo(BigDecimal.ZERO) > 0 && prixPromo.compareTo(prix) < 0;
    }

    public Double getPourcentageRemise() {
        if (!isEnPromotion()) return null;
        return (1 - prixPromo.doubleValue() / prix.doubleValue()) * 100;
    }
}