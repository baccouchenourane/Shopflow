package com.nourane.shopflow.controller;

import com.nourane.shopflow.entity.User;
import com.nourane.shopflow.exception.ResourceNotFoundException;
import com.nourane.shopflow.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Utilisateurs", description = "Gestion admin des utilisateurs")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<UserSummary>> getAllUsers(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<UserSummary> result = userRepository.findAll(pageable)
                .map(u -> new UserSummary(u.getId(), u.getEmail(), u.getPrenom(),
                        u.getNom(), u.getRole().name(), u.isActif()));
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/toggle-actif")
    public ResponseEntity<Void> toggleActif(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
        user.setActif(!user.isActif());
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}