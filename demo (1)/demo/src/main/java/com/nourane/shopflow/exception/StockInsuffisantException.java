package com.nourane.shopflow.exception;

public class StockInsuffisantException extends RuntimeException {
    public StockInsuffisantException(String productNom, int demande, int disponible) {
        super(String.format("Stock insuffisant pour '%s' : demandé=%d, disponible=%d",
                productNom, demande, disponible));
    }
}