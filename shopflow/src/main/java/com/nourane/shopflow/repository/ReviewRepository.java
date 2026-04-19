package com.nourane.shopflow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.nourane.shopflow.entity.Review;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductIdAndApprouveTrue(Long productId, Pageable pageable);

    @Query("SELECT AVG(r.note) FROM Review r WHERE r.product.id = :productId AND r.approuve = true")
    Optional<Double> calculateNoteMoyenne(@Param("productId") Long productId);

    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);

    // ✅ CORRECTION : la query JPQL précédente était invalide (JOIN entre entités non liées).
    //    On passe correctement par Order → items → product avec le statut DELIVERED.
    @Query("""
        SELECT COUNT(o) > 0
        FROM Order o
        JOIN o.items oi
        WHERE o.customer.id = :customerId
          AND oi.product.id = :productId
          AND o.statut = com.nourane.shopflow.entity.enums.OrderStatus.DELIVERED
        """)
    boolean hasCustomerBoughtProduct(@Param("customerId") Long customerId,
                                     @Param("productId") Long productId);

    Page<Review> findByApprouveFalse(Pageable pageable);
}