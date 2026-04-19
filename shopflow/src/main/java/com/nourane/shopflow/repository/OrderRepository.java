package com.nourane.shopflow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.nourane.shopflow.entity.Order;
import com.nourane.shopflow.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    Optional<Order> findByNumeroCommande(String numeroCommande);

    @Query("SELECT COALESCE(SUM(o.totalTTC), 0) FROM Order o WHERE o.statut = 'DELIVERED'")
    BigDecimal calculateChiffreAffairesGlobal();

    @Query("SELECT COALESCE(SUM(o.totalTTC), 0) FROM Order o " +
           "JOIN o.items oi WHERE oi.product.seller.id = :sellerId AND o.statut = 'DELIVERED'")
    BigDecimal calculateRevenuVendeur(@Param("sellerId") Long sellerId);

    @Query("SELECT o FROM Order o JOIN o.items oi WHERE oi.product.seller.id = :sellerId")
    Page<Order> findBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.statut = :statut")
    long countByStatut(@Param("statut") OrderStatus statut);

    @Query("SELECT o FROM Order o WHERE o.dateCommande >= :depuis ORDER BY o.dateCommande DESC")
    List<Order> findCommandesRecentes(@Param("depuis") LocalDateTime depuis);

    @Query("SELECT o FROM Order o JOIN o.items oi WHERE oi.product.seller.id = :sellerId AND o.statut = 'PENDING'")
    List<Order> findCommandesEnAttentePourVendeur(@Param("sellerId") Long sellerId);
}