package com.nourane.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;
import com.nourane.shopflow.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "items")
@ToString(exclude = "items")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus statut = OrderStatus.PENDING;

    @Column(unique = true, nullable = false)
    private String numeroCommande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address adresseLivraison;

    @Column(precision = 10, scale = 2)
    private BigDecimal sousTotal;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal fraisLivraison = BigDecimal.valueOf(5.0);

    @Column(precision = 10, scale = 2)
    private BigDecimal totalTTC;

    @Builder.Default
    private LocalDateTime dateCommande = LocalDateTime.now();

    @Builder.Default
    private boolean isNew = true;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal remiseCoupon = BigDecimal.ZERO;
} 
