package com.farmasystem.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductRequest {
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    @NotBlank(message = "El SKU es obligatorio")
    private String sku;

    private String barcode; // Puede ser opcional

    @NotNull(message = "El precio de venta es obligatorio")
    @Positive(message = "El precio de venta debe ser mayor a 0")
    private BigDecimal salePrice;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer minStock;

    private String provider; 

    @Valid // Valida también el objeto anidado
    private BatchRequest initialBatch;

    @Data
    public static class BatchRequest {
        @NotBlank(message = "El código de lote es obligatorio")
        private String batchCode;

        @NotNull(message = "La fecha de vencimiento es obligatoria")
        @Future(message = "La fecha de vencimiento debe ser una fecha futura") 
        private LocalDate expirationDate;

        @NotNull(message = "El stock inicial es obligatorio")
        @Positive(message = "El stock inicial debe ser mayor a 0")
        private Integer stock;

        @NotNull(message = "El precio de compra es obligatorio")
        @PositiveOrZero(message = "El precio de compra no puede ser negativo")
        private BigDecimal purchasePrice;
    }
}