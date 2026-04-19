package com.nourane.shopflow.dto.coupon;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.nourane.shopflow.entity.enums.CouponType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponDTOs {

    @Data
    public static class CouponRequest {
        @NotBlank
        private String code;

        @NotNull
        private CouponType type;

        @NotNull @DecimalMin("0.01")
        private BigDecimal valeur;

        private LocalDateTime dateExpiration;
        private Integer usagesMax;
    }

    @Data
    public static class CouponResponse {
        private Long id;
        private String code;
        private CouponType type;
        private BigDecimal valeur;
        private LocalDateTime dateExpiration;
        private Integer usagesMax;
        private Integer usagesActuels;
        private boolean actif;
        private boolean valide;
    }
}