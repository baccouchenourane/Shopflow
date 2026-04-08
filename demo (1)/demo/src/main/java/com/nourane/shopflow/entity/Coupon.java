package com.nourane.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;
import com.nourane.shopflow.entity.enums.CouponType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valeur;

    private LocalDateTime dateExpiration;

    private Integer usagesMax;

    @Builder.Default
    private Integer usagesActuels = 0;

    @Builder.Default
    private boolean actif = true;

    public boolean isValide() {
        if (!actif) return false;
        if (dateExpiration != null && LocalDateTime.now().isAfter(dateExpiration)) return false;
        if (usagesMax != null && usagesActuels >= usagesMax) return false;
        return true;
    }
}