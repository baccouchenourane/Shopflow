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

    @Query("SELECT COUNT(r) > 0 FROM Review r " +
           "JOIN OrderItem oi ON oi.product = r.product " +
           "JOIN Order o ON oi.order = o " +
           "WHERE o.customer.id = :customerId AND r.product.id = :productId AND o.statut = 'DELIVERED'")
    boolean hasCustomerBoughtProduct(@Param("customerId") Long customerId, @Param("productId") Long productId);

    Page<Review> findByApprouveFalse(Pageable pageable);
}