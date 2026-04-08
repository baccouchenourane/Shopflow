package com.nourane.shopflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.nourane.shopflow.dto.cart.CartDTOs.*;
import com.nourane.shopflow.service.CartService;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Panier", description = "Gestion du panier d'achat")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Récupérer le panier du client connecté")
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @PostMapping("/items")
    @Operation(summary = "Ajouter un article au panier")
    public ResponseEntity<CartResponse> addItem(
            @Valid @RequestBody AddItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.addItem(request, userDetails.getUsername()));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Modifier la quantité d'un article")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.updateItem(itemId, request, userDetails.getUsername()));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Retirer un article du panier")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.removeItem(itemId, userDetails.getUsername()));
    }

    @PostMapping("/coupon")
    @Operation(summary = "Appliquer un code promo")
    public ResponseEntity<CartResponse> applyCoupon(
            @Valid @RequestBody CouponRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.applyCoupon(request, userDetails.getUsername()));
    }

    @DeleteMapping("/coupon")
    @Operation(summary = "Retirer le code promo")
    public ResponseEntity<CartResponse> removeCoupon(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.removeCoupon(userDetails.getUsername()));
    }
}