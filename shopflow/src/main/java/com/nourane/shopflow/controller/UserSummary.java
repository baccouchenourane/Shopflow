package com.nourane.shopflow.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserSummary {
    public UserSummary(Long id, @Email @NotBlank String email, @NotBlank String prenom, @NotBlank String nom, String name, boolean actif) {
    }
}
