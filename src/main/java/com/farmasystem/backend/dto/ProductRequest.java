package com.farmasystem.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductRequest {
    private String name;
    private String sku;
    private String barcode;
    private BigDecimal salePrice;
    private Integer minStock;
    private String provider; 
    private BatchRequest initialBatch;

    @Data
    public static class BatchRequest {
        private String batchCode;
        private LocalDate expirationDate;
        private Integer stock;
        private BigDecimal purchasePrice;
    }
}