package com.nourane.shopflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nourane.shopflow.dto.address.AddressDTOs.*;
import com.nourane.shopflow.entity.Address;
import com.nourane.shopflow.entity.User;
import com.nourane.shopflow.exception.ResourceNotFoundException;
import com.nourane.shopflow.repository.AddressRepository;
import com.nourane.shopflow.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

// ✅ AJOUT : Service adresses manquant
@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AddressResponse> getAll(String email) {
        User user = getUser(email);
        return addressRepository.findByUserId(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AddressResponse create(AddressRequest request, String email) {
        User user = getUser(email);

        // Si c'est la première adresse ou marquée principale, retirer l'ancienne principale
        if (request.isPrincipal()) {
            addressRepository.findByUserId(user.getId())
                    .forEach(a -> { a.setPrincipal(false); addressRepository.save(a); });
        }

        Address address = Address.builder()
                .user(user)
                .rue(request.getRue())
                .ville(request.getVille())
                .codePostal(request.getCodePostal())
                .pays(request.getPays())
                .principal(request.isPrincipal())
                .build();

        return toResponse(addressRepository.save(address));
    }

    public AddressResponse setPrincipal(Long id, String email) {
        User user = getUser(email);
        Address target = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse", id));

        if (!target.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cette adresse ne vous appartient pas");
        }

        // Retirer principal des autres
        addressRepository.findByUserId(user.getId())
                .forEach(a -> { a.setPrincipal(a.getId().equals(id)); addressRepository.save(a); });

        return toResponse(target);
    }

    public void delete(Long id, String email) {
        User user = getUser(email);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse", id));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cette adresse ne vous appartient pas");
        }

        addressRepository.delete(address);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable", "email", email));
    }

    private AddressResponse toResponse(Address a) {
        AddressResponse r = new AddressResponse();
        r.setId(a.getId());
        r.setRue(a.getRue());
        r.setVille(a.getVille());
        r.setCodePostal(a.getCodePostal());
        r.setPays(a.getPays());
        r.setPrincipal(a.isPrincipal());
        return r;
    }
}