package com.farmasystem.backend.dto;

import lombok.Data; // <--- Magia
import java.util.List;

@Data 
public class SaleRequest {
    private Long sellerId;
    private String clientDni;
    private List<SaleItemRequest> items;

    @Data
    public static class SaleItemRequest {
        private Long productId;
        private Integer quantity;
    }
}