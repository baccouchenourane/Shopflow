package com.nourane.shopflow.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.nourane.shopflow.entity.enums.Role;

public class AuthDTOs {

    @Data
    public static class RegisterRequest {
        @Email(message = "Email invalide")
        @NotBlank
        private String email;

        @NotBlank
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).*$",
                 message = "Le mot de passe doit contenir au moins une majuscule et un chiffre")
        private String motDePasse;

        @NotBlank
        private String prenom;

        @NotBlank
        private String nom;

        private Role role = Role.CUSTOMER;

        // Champs vendeur optionnels
        private String nomBoutique;
        private String descriptionBoutique;
    }

    @Data
    public static class LoginRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String motDePasse;
    }

    @Data
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String type = "Bearer";
        private String email;
        private String role;
        private String prenom;
        private String nom;
    }

    @Data
    public static class RefreshRequest {
        @NotBlank
        private String refreshToken;
    }

    @Data
    public static class ResetPasswordRequest {
        @Email @NotBlank
        private String email;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank
        private String token;
        @NotBlank @Size(min = 8)
        private String nouveauMotDePasse;
    }
}