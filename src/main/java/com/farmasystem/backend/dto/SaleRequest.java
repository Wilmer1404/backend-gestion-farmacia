package com.farmasystem.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SaleRequest {
    private Long sellerId;
    private String clientDni;
    
    private String clientName; 

    private List<SaleItemRequest> items;

    @Data
    public static class SaleItemRequest {
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productId;
        
        private Integer quantity;
    }
}