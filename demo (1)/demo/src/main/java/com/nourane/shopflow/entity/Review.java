package com.nourane.shopflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Min(1) @Max(5)
    @Column(nullable = false)
    private Integer note;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Builder.Default
    private boolean approuve = false;
}