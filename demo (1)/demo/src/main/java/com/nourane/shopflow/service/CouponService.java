package com.nourane.shopflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nourane.shopflow.dto.coupon.CouponDTOs.*;
import com.nourane.shopflow.entity.Coupon;
import com.nourane.shopflow.exception.BusinessException;
import com.nourane.shopflow.exception.ResourceNotFoundException;
import com.nourane.shopflow.repository.CouponRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public List<CouponResponse> getAll() {
        return couponRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public CouponResponse create(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Un coupon avec ce code existe déjà");
        }
        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .type(request.getType())
                .valeur(request.getValeur())
                .dateExpiration(request.getDateExpiration())
                .usagesMax(request.getUsagesMax())
                .actif(true)
                .build();
        return toResponse(couponRepository.save(coupon));
    }

    public CouponResponse update(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", id));
        coupon.setType(request.getType());
        coupon.setValeur(request.getValeur());
        coupon.setDateExpiration(request.getDateExpiration());
        coupon.setUsagesMax(request.getUsagesMax());
        return toResponse(couponRepository.save(coupon));
    }

    public void delete(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", id));
        coupon.setActif(false);
        couponRepository.save(coupon);
    }

    @Transactional(readOnly = true)
    public CouponResponse validate(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BusinessException("Code promo invalide"));
        if (!coupon.isValide()) {
            throw new BusinessException("Ce coupon est expiré ou épuisé");
        }
        return toResponse(coupon);
    }

    private CouponResponse toResponse(Coupon coupon) {
        CouponResponse r = new CouponResponse();
        r.setId(coupon.getId());
        r.setCode(coupon.getCode());
        r.setType(coupon.getType());
        r.setValeur(coupon.getValeur());
        r.setDateExpiration(coupon.getDateExpiration());
        r.setUsagesMax(coupon.getUsagesMax());
        r.setUsagesActuels(coupon.getUsagesActuels());
        r.setActif(coupon.isActif());
        r.setValide(coupon.isValide());
        return r;
    }
}