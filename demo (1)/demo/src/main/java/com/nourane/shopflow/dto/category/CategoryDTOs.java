package com.nourane.shopflow.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

public class CategoryDTOs {

    @Data
    public static class CategoryRequest {
        @NotBlank
        private String nom;
        private String description;
        private Long parentId;
    }

    @Data
    public static class CategoryResponse {
        private Long id;
        private String nom;
        private String description;
        private Long parentId;
        private List<CategoryResponse> children;
    }
}