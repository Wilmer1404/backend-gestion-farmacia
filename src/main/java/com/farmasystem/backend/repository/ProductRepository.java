package com.farmasystem.backend.repository;

import com.farmasystem.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.batches WHERE p.deleted = false")
    List<Product> findAllWithBatches();

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.batches WHERE p.id = :id")
    Optional<Product> findByIdWithBatches(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    List<Product> findAllActiveProducts();

    @Query("SELECT p.name, SUM(d.quantity), SUM(d.subtotal) " +
           "FROM SaleDetail d JOIN d.product p " +
           "GROUP BY p.name " +
           "ORDER BY SUM(d.quantity) DESC")
    List<Object[]> getTopSellingProductsData();

    // --- NUEVOS MÉTODOS PARA VALIDACIÓN (Spring Data JPA los implementa automáticamente) ---
    boolean existsBySku(String sku);
    boolean existsByBarcode(String barcode);
}