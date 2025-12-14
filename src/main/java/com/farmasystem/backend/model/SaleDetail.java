package com.farmasystem.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // <--- IMPORTAR ESTO
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sale_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SaleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    @JsonIgnore // <--- ESTO ES LA CLAVE. Evita el bucle infinito.
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}