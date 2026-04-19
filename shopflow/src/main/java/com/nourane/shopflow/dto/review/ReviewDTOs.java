package com.nourane.shopflow.dto.review;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

public class ReviewDTOs {

    @Data
    public static class ReviewRequest {
        @NotNull
        private Long productId;

        @NotNull @Min(1) @Max(5)
        private Integer note;

        private String commentaire;
    }

    @Data
    public static class ReviewResponse {
        private Long id;
        private String clientNom;
        private Integer note;
        private String commentaire;
        private LocalDateTime dateCreation;
        private boolean approuve;
    }
}