package com.nourane.shopflow.repository.specification;


import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import com.nourane.shopflow.entity.Category;
import com.nourane.shopflow.entity.Product;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> actif() {
        return (root, query, cb) -> cb.isTrue(root.get("actif"));
    }

    public static Specification<Product> nomContient(String nom) {
        return (root, query, cb) ->
            nom == null ? null : cb.like(cb.lower(root.get("nom")), "%" + nom.toLowerCase() + "%");
    }

    public static Specification<Product> prixMin(BigDecimal min) {
        return (root, query, cb) ->
            min == null ? null : cb.greaterThanOrEqualTo(root.get("prix"), min);
    }

    public static Specification<Product> prixMax(BigDecimal max) {
        return (root, query, cb) ->
            max == null ? null : cb.lessThanOrEqualTo(root.get("prix"), max);
    }

    public static Specification<Product> parVendeur(Long sellerId) {
        return (root, query, cb) ->
            sellerId == null ? null : cb.equal(root.get("seller").get("id"), sellerId);
    }

    public static Specification<Product> parCategorie(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;
            Join<Product, Category> categories = root.join("categories");
            return cb.equal(categories.get("id"), categoryId);
        };
    }

    public static Specification<Product> enPromotion() {
        return (root, query, cb) ->
            cb.and(
                cb.isNotNull(root.get("prixPromo")),
                cb.lessThan(root.get("prixPromo"), root.get("prix"))
            );
    }
}