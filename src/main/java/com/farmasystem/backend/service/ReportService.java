package com.farmasystem.backend.service;

import com.farmasystem.backend.dto.ReportDTO;
import com.farmasystem.backend.model.Batch;
import com.farmasystem.backend.repository.ProductRepository;
import com.farmasystem.backend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date; // Importante para manejar fechas de SQL
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

        // Lógica para contar stock bajo (usando findAllActiveProducts para optimizar)
        long lowStock = productRepository.findAllWithBatches().stream() // <--- CAMBIO AQUÍ
                .filter(p -> {
                    int totalStock = p.getBatches().stream().mapToInt(Batch::getStock).sum();
                    return totalStock <= p.getMinStock();
                })
                .count();

        return new ReportDTO.KpiStats(
                salesToday != null ? salesToday : BigDecimal.ZERO,
                salesMonth != null ? salesMonth : BigDecimal.ZERO,
                countToday != null ? countToday : 0L,
                lowStock);
    }

    public List<ReportDTO.SalesByDate> getSalesChart() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // 1. Obtenemos datos crudos del repo (Object[])
        List<Object[]> rawData = saleRepository.getSalesDataGroupedByDate(sevenDaysAgo);

        // 2. Transformamos manualmente a DTO
        List<ReportDTO.SalesByDate> chartData = new ArrayList<>();

        for (Object[] row : rawData) {
            // row[0] es la Fecha, row[1] es el Total
            String dateStr = row[0].toString();
            BigDecimal total = (BigDecimal) row[1];

            chartData.add(new ReportDTO.SalesByDate(dateStr, total));
        }

        return chartData;
    }

    public List<ReportDTO.TopProduct> getTopProducts() {
        // 1. Obtenemos datos crudos (Object[])
        List<Object[]> rawData = productRepository.getTopSellingProductsData();

        // 2. Transformamos manualmente a DTO (Limitamos a 5 aquí si la query no lo
        // tiene)
        List<ReportDTO.TopProduct> topProducts = new ArrayList<>();

        for (Object[] row : rawData) {
            if (topProducts.size() >= 5)
                break; // Aseguramos máx 5

            String name = (String) row[0];
            Long quantity = ((Number) row[1]).longValue(); // Casteo seguro para números
            BigDecimal revenue = (BigDecimal) row[2];

            topProducts.add(new ReportDTO.TopProduct(name, quantity, revenue));
        }

        return topProducts;
    }
}