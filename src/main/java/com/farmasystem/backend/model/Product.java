package com.farmasystem.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction; 
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@SQLDelete(sql = "UPDATE products SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false") 
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String sku;

    private String barcode;

    @Column(name = "sale_price", nullable = false)
    private BigDecimal salePrice;

    @Column(name = "min_stock")
    private Integer minStock;

    @Column(name = "provider")
    private String provider; 

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Batch> batches;
}