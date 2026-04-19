package com.nourane.shopflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nourane.shopflow.dto.category.CategoryDTOs.*;
import com.nourane.shopflow.entity.Category;
import com.nourane.shopflow.exception.BusinessException;
import com.nourane.shopflow.exception.ResourceNotFoundException;
import com.nourane.shopflow.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getTree() {
        return categoryRepository.findByParentIsNull()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNom(request.getNom())) {
            throw new BusinessException("Une catégorie avec ce nom existe déjà");
        }

        Category category = new Category();
        category.setNom(request.getNom());
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie parent", request.getParentId()));
            category.setParent(parent);
        }

        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id));

        category.setNom(request.getNom());
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BusinessException("Une catégorie ne peut pas être son propre parent");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie parent", request.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id));
        if (!category.getProducts().isEmpty()) {
            throw new BusinessException("Impossible de supprimer une catégorie contenant des produits");
        }
        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category) {
        CategoryResponse r = new CategoryResponse();
        r.setId(category.getId());
        r.setNom(category.getNom());
        r.setDescription(category.getDescription());
        r.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        r.setChildren(category.getChildren().stream().map(this::toResponse).collect(Collectors.toList()));
        return r;
    }
}