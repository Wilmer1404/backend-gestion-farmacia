package com.farmasystem.backend.repository;

import com.farmasystem.backend.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    
    @Query("SELECT b FROM Batch b WHERE b.product.id = :productId AND b.stock > 0 ORDER BY b.expirationDate ASC")
    List<Batch> findAvailableBatches(Long productId);
}