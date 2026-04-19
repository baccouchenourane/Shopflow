package com.nourane.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String attribut; // ex: "Taille", "Couleur"

    @Column(nullable = false)
    private String valeur;   // ex: "M", "Rouge"

    @Builder.Default
    private Integer stockSupplementaire = 0;

    @Builder.Default
    private BigDecimal prixDelta = BigDecimal.ZERO;
}