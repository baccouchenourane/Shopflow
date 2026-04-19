package com.nourane.shopflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nourane.shopflow.entity.CartItem;

import java.util.List;

// ✅ AJOUT : Repository manquant pour CartItem
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
    void deleteByCartId(Long cartId);
}