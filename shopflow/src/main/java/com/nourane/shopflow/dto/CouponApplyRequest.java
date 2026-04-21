package com.nourane.shopflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour appliquer un coupon")
public class CouponApplyRequest {

    @NotBlank(message = "Le code promo est requis")
    @Schema(description = "Code promo", example = "BIENVENUE10")
    private String code;
}