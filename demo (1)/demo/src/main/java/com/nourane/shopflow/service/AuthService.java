package com.nourane.shopflow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nourane.shopflow.dto.auth.AuthDTOs.*;
import com.nourane.shopflow.entity.*;
import com.nourane.shopflow.entity.enums.Role;
import com.nourane.shopflow.exception.BusinessException;
import com.nourane.shopflow.exception.ResourceNotFoundException;
import com.nourane.shopflow.repository.*;
import com.nourane.shopflow.security.JwtService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CartRepository cartRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiration;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Un compte existe déjà avec cet email");
        }

        User user = User.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .role(request.getRole() != null ? request.getRole() : Role.CUSTOMER)
                .actif(true)
                .build();

        user = userRepository.save(user);

        // Créer profil vendeur si nécessaire
        if (user.getRole() == Role.SELLER) {
            if (request.getNomBoutique() == null || request.getNomBoutique().isBlank()) {
                throw new BusinessException("Le nom de boutique est obligatoire pour un vendeur");
            }
            SellerProfile profile = SellerProfile.builder()
                    .user(user)
                    .nomBoutique(request.getNomBoutique())
                    .description(request.getDescriptionBoutique())
                    .build();
            sellerProfileRepository.save(profile);
        }

        // Créer panier pour les clients
        if (user.getRole() == Role.CUSTOMER) {
            Cart cart = Cart.builder().customer(user).build();
            cartRepository.save(cart);
        }

        log.info("Nouvel utilisateur enregistré : {} ({})", user.getEmail(), user.getRole());

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", null));

        if (!user.isActif()) {
            throw new BusinessException("Votre compte a été désactivé");
        }

        // Révoquer les anciens refresh tokens
        refreshTokenRepository.revokeAllUserTokens(user.getId());

        log.info("Connexion réussie : {}", user.getEmail());
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Refresh token invalide"));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new BusinessException("Refresh token expiré ou révoqué");
        }

        User user = refreshToken.getUser();
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return buildAuthResponse(user);
    }

    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    public void resetPasswordRequest(ResetPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            userRepository.save(user);
            // En prod : envoyer l'email avec le token
            log.info("Token de réinitialisation pour {} : {}", user.getEmail(), token);
        });
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new BusinessException("Token invalide ou expiré"));

        user.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .build();
        refreshTokenRepository.save(refreshToken);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken.getToken());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setPrenom(user.getPrenom());
        response.setNom(user.getNom());
        return response;
    }
}