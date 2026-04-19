package com.nourane.shopflow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.nourane.shopflow.entity.Product;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findByActifTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.actif = true AND " +
           "(LOWER(p.nom) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Product> searchFullText(@Param("q") String q, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.actif = true AND p.prixPromo IS NOT NULL AND p.prixPromo < p.prix")
    Page<Product> findEnPromotion(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.actif = true ORDER BY p.totalVentes DESC")
    List<Product> findTop10BySales(Pageable pageable);

    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.actif = true AND p.stock <= :seuil AND p.seller.id = :sellerId")
    List<Product> findStockFaible(@Param("seuil") int seuil, @Param("sellerId") Long sellerId);

    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId AND p.actif = true")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.actif = true AND " +
           "(:minPrix IS NULL OR p.prix >= :minPrix) AND " +
           "(:maxPrix IS NULL OR p.prix <= :maxPrix)")
    Page<Product> findByPrixRange(@Param("minPrix") BigDecimal minPrix,
                                   @Param("maxPrix") BigDecimal maxPrix,
                                   Pageable pageable);
}