package com.farmasystem.backend.repository;

import com.farmasystem.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Consulta optimizada para evitar N+1 queries al cargar productos con sus batches
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.batches WHERE p.deleted = false")
    List<Product> findAllWithBatches();

    // Consulta simple para obtener todos los productos activos (sin batches para evitar lazy loading)
    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    List<Product> findAllActiveProducts();

    // Consulta simplificada - devolvemos datos brutos para top productos
    @Query("SELECT p.name, SUM(d.quantity), SUM(d.subtotal) " +
           "FROM SaleDetail d JOIN d.product p " +
           "GROUP BY p.name " +
           "ORDER BY SUM(d.quantity) DESC")
    List<Object[]> getTopSellingProductsData();
}