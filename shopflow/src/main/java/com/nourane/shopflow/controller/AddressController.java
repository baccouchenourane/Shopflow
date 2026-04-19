package com.nourane.shopflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.nourane.shopflow.dto.address.AddressDTOs.*;
import com.nourane.shopflow.service.AddressService;

import java.util.List;

// ✅ AJOUT : Controller adresses manquant (référencé dans les specs mais absent du code)
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Adresses", description = "Gestion des adresses de livraison du client")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Lister mes adresses")
    public ResponseEntity<List<AddressResponse>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(addressService.getAll(userDetails.getUsername()));
    }

    @PostMapping
    @Operation(summary = "Ajouter une adresse")
    public ResponseEntity<AddressResponse> create(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.create(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}/principal")
    @Operation(summary = "Définir comme adresse principale")
    public ResponseEntity<AddressResponse> setPrincipal(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(addressService.setPrincipal(id, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une adresse")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        addressService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}