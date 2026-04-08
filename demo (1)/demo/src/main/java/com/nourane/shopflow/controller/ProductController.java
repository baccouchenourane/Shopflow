package com.nourane.shopflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.nourane.shopflow.dto.product.ProductDTOs.*;
import com.nourane.shopflow.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "Catalogue, recherche et gestion des produits")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Liste paginée de produits avec filtres")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "dateCreation") String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrix,
            @RequestParam(required = false) BigDecimal maxPrix,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Boolean promo) {
        return ResponseEntity.ok(
            productService.getAllProducts(page, size, sort, categoryId, minPrix, maxPrix, sellerId, promo)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un produit avec variantes et avis")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Recherche full-text par nom/description")
    public ResponseEntity<Page<ProductSummary>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(productService.search(q, page, size));
    }

    @GetMapping("/top-selling")
    @Operation(summary = "Top 10 meilleures ventes")
    public ResponseEntity<List<ProductSummary>> getTopSelling() {
        return ResponseEntity.ok(productService.getTopSelling());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Créer un produit", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Modifier un produit", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(productService.update(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Désactiver un produit (soft delete)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        productService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}