
package com.nourane.shopflow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nourane.shopflow.dto.product.ProductDTOs.*;
import com.nourane.shopflow.entity.*;
import com.nourane.shopflow.entity.enums.Role;
import com.nourane.shopflow.exception.BusinessException;
import com.nourane.shopflow.exception.ResourceNotFoundException;
import com.nourane.shopflow.repository.*;
import com.nourane.shopflow.repository.specification.ProductSpecification;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(int page, int size, String sort,
                                                Long categoryId, BigDecimal minPrix, BigDecimal maxPrix,
                                                Long sellerId, Boolean promo) {
        Pageable pageable = buildPageable(page, size, sort);

        Specification<Product> spec = Specification.where(ProductSpecification.actif());

        if (categoryId != null)  spec = spec.and(ProductSpecification.parCategorie(categoryId));
        if (minPrix != null)     spec = spec.and(ProductSpecification.prixMin(minPrix));
        if (maxPrix != null)     spec = spec.and(ProductSpecification.prixMax(maxPrix));
        if (sellerId != null)    spec = spec.and(ProductSpecification.parVendeur(sellerId));
        if (Boolean.TRUE.equals(promo)) spec = spec.and(ProductSpecification.enPromotion());

        return productRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductSummary> search(String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.searchFullText(q, pageable).map(this::toSummary);
    }

    @Transactional(readOnly = true)
    public List<ProductSummary> getTopSelling() {
        Pageable top10 = PageRequest.of(0, 10);
        return productRepository.findTop10BySales(top10).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    public ProductResponse create(ProductRequest request, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Vendeur introuvable"));

        Product product = Product.builder()
                .seller(seller)
                .nom(request.getNom())
                .description(request.getDescription())
                .prix(request.getPrix())
                .prixPromo(request.getPrixPromo())
                .stock(request.getStock())
                .images(request.getImages() != null ? request.getImages() : List.of())
                .build();

        // Associer catégories
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            product.setCategories(categories);
        }

        // Ajouter variantes
        if (request.getVariants() != null) {
            List<ProductVariant> variants = request.getVariants().stream().map(vr -> {
                ProductVariant v = new ProductVariant();
                v.setProduct(product);
                v.setAttribut(vr.getAttribut());
                v.setValeur(vr.getValeur());
                v.setStockSupplementaire(vr.getStockSupplementaire());
                v.setPrixDelta(vr.getPrixDelta());
                return v;
            }).collect(Collectors.toList());
            product.setVariants(variants);
        }

        Product saved = productRepository.save(product);
        log.info("Produit créé : {} par {}", saved.getNom(), sellerEmail);
        return toResponse(saved);
    }

    public ProductResponse update(Long id, ProductRequest request, String userEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        // Vérifier que le vendeur est propriétaire ou que c'est un admin
        if (user.getRole() != Role.ADMIN && !product.getSeller().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        product.setNom(request.getNom());
        product.setDescription(request.getDescription());
        product.setPrix(request.getPrix());
        product.setPrixPromo(request.getPrixPromo());
        product.setStock(request.getStock());
        if (request.getImages() != null) product.setImages(request.getImages());

        if (request.getCategoryIds() != null) {
            product.setCategories(categoryRepository.findAllById(request.getCategoryIds()));
        }

        return toResponse(productRepository.save(product));
    }

    public void delete(Long id, String userEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (user.getRole() != Role.ADMIN && !product.getSeller().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer ce produit");
        }

        // Soft delete
        product.setActif(false);
        productRepository.save(product);
        log.info("Produit désactivé (soft delete) : {}", id);
    }

    // ─── Mappers ─────────────────────────────────────────────────────────────────

    public ProductResponse toResponse(Product product) {
        ProductResponse r = new ProductResponse();
        r.setId(product.getId());
        r.setNom(product.getNom());
        r.setDescription(product.getDescription());
        r.setPrix(product.getPrix());
        r.setPrixPromo(product.getPrixPromo());
        r.setEnPromotion(product.isEnPromotion());
        r.setPourcentageRemise(product.getPourcentageRemise());
        r.setStock(product.getStock());
        r.setActif(product.isActif());
        r.setDateCreation(product.getDateCreation());
        r.setTotalVentes(product.getTotalVentes());
        r.setImages(product.getImages());
        r.setSellerId(product.getSeller().getId());
        r.setNomBoutique(product.getSeller().getSellerProfile() != null
                ? product.getSeller().getSellerProfile().getNomBoutique() : null);

        r.setCategories(product.getCategories().stream().map(c -> {
            CategorySummary cs = new CategorySummary();
            cs.setId(c.getId());
            cs.setNom(c.getNom());
            return cs;
        }).collect(Collectors.toList()));

        r.setVariants(product.getVariants().stream().map(v -> {
            VariantResponse vr = new VariantResponse();
            vr.setId(v.getId());
            vr.setAttribut(v.getAttribut());
            vr.setValeur(v.getValeur());
            vr.setStockSupplementaire(v.getStockSupplementaire());
            vr.setPrixDelta(v.getPrixDelta());
            return vr;
        }).collect(Collectors.toList()));

        r.setNoteMoyenne(reviewRepository.calculateNoteMoyenne(product.getId()).orElse(null));
        return r;
    }

    public ProductSummary toSummary(Product product) {
        ProductSummary s = new ProductSummary();
        s.setId(product.getId());
        s.setNom(product.getNom());
        s.setPrix(product.getPrix());
        s.setPrixPromo(product.getPrixPromo());
        s.setEnPromotion(product.isEnPromotion());
        s.setStock(product.getStock());
        s.setImageUrl(product.getImages().isEmpty() ? null : product.getImages().get(0));
        s.setNoteMoyenne(reviewRepository.calculateNoteMoyenne(product.getId()).orElse(null));
        return s;
    }

    private Pageable buildPageable(int page, int size, String sort) {
        Sort s = switch (sort != null ? sort : "dateCreation") {
            case "prix_asc"    -> Sort.by("prix").ascending();
            case "prix_desc"   -> Sort.by("prix").descending();
            case "popularite"  -> Sort.by("totalVentes").descending();
            default            -> Sort.by("dateCreation").descending();
        };
        return PageRequest.of(page, size, s);
    }
}