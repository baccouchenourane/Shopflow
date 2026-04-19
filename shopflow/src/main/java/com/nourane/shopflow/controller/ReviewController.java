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
import com.nourane.shopflow.dto.review.ReviewDTOs;
import com.nourane.shopflow.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Avis", description = "Notation et modération des avis produits")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Poster un avis (achat vérifié requis)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewDTOs.ReviewResponse> create(
            @Valid @RequestBody ReviewDTOs.ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.create(request, userDetails.getUsername()));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Avis approuvés d'un produit")
    public ResponseEntity<Page<ReviewDTOs.ReviewResponse>> getByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getByProduct(productId, page, size));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approuver un avis (ADMIN)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewDTOs.ReviewResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approve(id));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Avis en attente de modération (ADMIN)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<ReviewDTOs.ReviewResponse>> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reviewService.getPending(page, size));
    }
}