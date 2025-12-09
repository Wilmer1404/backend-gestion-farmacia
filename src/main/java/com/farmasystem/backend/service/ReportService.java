package com.farmasystem.backend.service;

import com.farmasystem.backend.dto.ReportDTO;
import com.farmasystem.backend.model.Batch;
import com.farmasystem.backend.repository.ProductRepository;
import com.farmasystem.backend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public ReportDTO.KpiStats getKpiStats() {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        BigDecimal salesToday = saleRepository.sumTotalSalesBetween(startOfDay, endOfDay);
        BigDecimal salesMonth = saleRepository.sumTotalSalesBetween(startOfMonth, endOfDay);
        Long countToday = saleRepository.countSalesBetween(startOfDay, endOfDay);

        // Para productos con bajo stock, usamos una consulta que carga también los batches
        long lowStock = productRepository.findAllWithBatches().stream()
            .filter(p -> {
                int totalStock = p.getBatches().stream().mapToInt(Batch::getStock).sum();
                return totalStock <= (p.getMinStock() != null ? p.getMinStock() : 0);
            })
            .count();

        return new ReportDTO.KpiStats(
            salesToday, 
            salesMonth, 
            countToday, 
            lowStock
        );
    }

    public List<ReportDTO.SalesByDate> getSalesChart() {
        // Últimos 7 días
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Object[]> rawData = saleRepository.getSalesDataGroupedByDate(sevenDaysAgo);
        
        // Convertimos Object[] a DTOs - manejamos diferentes tipos de fecha
        return rawData.stream()
            .map(row -> {
                Object dateObj = row[0];
                BigDecimal total = (BigDecimal) row[1];
                
                String dateStr;
                if (dateObj instanceof LocalDate) {
                    dateStr = ((LocalDate) dateObj).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } else if (dateObj instanceof java.sql.Date) {
                    dateStr = ((java.sql.Date) dateObj).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } else {
                    dateStr = dateObj.toString();
                }
                
                return new ReportDTO.SalesByDate(dateStr, total);
            })
            .collect(Collectors.toList());
    }

    public List<ReportDTO.TopProduct> getTopProducts() {
        List<Object[]> rawData = productRepository.getTopSellingProductsData();
        
        // Convertimos Object[] a DTOs y limitamos a 5 resultados
        return rawData.stream()
            .limit(5)
            .map(row -> {
                String productName = (String) row[0];
                Long quantitySold = (Long) row[1];
                BigDecimal totalRevenue = (BigDecimal) row[2];
                return new ReportDTO.TopProduct(productName, quantitySold, totalRevenue);
            })
            .collect(Collectors.toList());
    }
}