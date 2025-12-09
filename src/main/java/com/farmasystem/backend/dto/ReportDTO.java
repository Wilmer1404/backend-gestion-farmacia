package com.farmasystem.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

public class ReportDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KpiStats {
        private BigDecimal totalSalesToday;
        private BigDecimal totalSalesMonth;
        private Long salesCountToday;
        private Long lowStockCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SalesByDate {
        private String date; 
        private BigDecimal total;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopProduct {
        private String productName;
        private Long quantitySold;
        private BigDecimal totalRevenue;
    }
}