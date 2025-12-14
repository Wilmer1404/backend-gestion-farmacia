package com.farmasystem.backend.repository;

import com.farmasystem.backend.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; 

public interface SaleRepository extends JpaRepository<Sale, Long> {

    // --- REPORTES (Ya los tenías) ---
    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE s.createdAt BETWEEN :start AND :end")
    BigDecimal sumTotalSalesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.createdAt BETWEEN :start AND :end")
    Long countSalesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Consulta nativa/raw para evitar problemas de Hibernate con DTOs internos
    @Query("SELECT CAST(s.createdAt AS DATE), SUM(s.total) " +
           "FROM Sale s WHERE s.createdAt >= :startDate " +
           "GROUP BY CAST(s.createdAt AS DATE) " +
           "ORDER BY CAST(s.createdAt AS DATE) ASC")
    List<Object[]> getSalesDataGroupedByDate(@Param("startDate") LocalDateTime startDate);

    // --- NUEVO: BUSCAR CLIENTE RECURRENTE ---
    // Busca la venta más reciente asociada a un DNI/RUC para obtener el nombre guardado
    Optional<Sale> findFirstByClientDocOrderByCreatedAtDesc(String clientDoc);
}