package com.nourane.shopflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nourane.shopflow.dto.review.ReviewDTOs.*;
import com.nourane.shopflow.entity.*;
import com.nourane.shopflow.exception.BusinessException;
import com.nourane.shopflow.exception.ResourceNotFoundException;
import com.nourane.shopflow.repository.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public ReviewResponse create(ReviewRequest request, String email) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable", "email", email));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", request.getProductId()));

        // Vérifier que le client a acheté le produit
        if (!reviewRepository.hasCustomerBoughtProduct(customer.getId(), product.getId())) {
            throw new BusinessException("Vous devez avoir acheté ce produit pour laisser un avis");
        }

        if (reviewRepository.existsByCustomerIdAndProductId(customer.getId(), product.getId())) {
            throw new BusinessException("Vous avez déjà laissé un avis sur ce produit");
        }

        Review review = Review.builder()
                .customer(customer)
                .product(product)
                .note(request.getNote())
                .commentaire(request.getCommentaire())
                .approuve(false)
                .build();

        return toResponse(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getByProduct(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        return reviewRepository.findByProductIdAndApprouveTrue(productId, pageable).map(this::toResponse);
    }

    public ReviewResponse approve(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", id));
        review.setApprouve(true);
        return toResponse(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getPending(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        return reviewRepository.findByApprouveFalse(pageable).map(this::toResponse);
    }

    private ReviewResponse toResponse(Review review) {
        ReviewResponse r = new ReviewResponse();
        r.setId(review.getId());
        r.setClientNom(review.getCustomer().getPrenom() + " " + review.getCustomer().getNom());
        r.setNote(review.getNote());
        r.setCommentaire(review.getCommentaire());
        r.setDateCreation(review.getDateCreation());
        r.setApprouve(review.isApprouve());
        return r;
    }
}
