package com.nourane.shopflow.entity.enums;

public enum OrderStatus {
    PENDING(),
    PAID(),
    PROCESSING(),
    SHIPPED(),
    DELIVERED(),
    CANCELLED(), REFUNDED();

    private String libelle = "";

    OrderStatus() {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}