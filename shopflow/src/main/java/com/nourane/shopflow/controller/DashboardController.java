package com.nourane.shopflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.nourane.shopflow.dto.dashboard.DashboardDTOs.*;
import com.nourane.shopflow.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Tableaux de bord par rôle")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Dashboard ADMIN — stats globales, top produits, top vendeurs")
    public ResponseEntity<AdminDashboard> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/seller")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Dashboard SELLER — revenus, commandes en attente, alertes stock")
    public ResponseEntity<SellerDashboard> getSellerDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.getSellerDashboard(userDetails.getUsername()));
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Dashboard CUSTOMER — mes commandes en cours, mes derniers avis")
    public ResponseEntity<CustomerDashboard> getCustomerDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.getCustomerDashboard(userDetails.getUsername()));
    }
}