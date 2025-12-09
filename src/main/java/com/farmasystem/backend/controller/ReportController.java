package com.farmasystem.backend.controller;

import com.farmasystem.backend.dto.ReportDTO;
import com.farmasystem.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {

    private final ReportService reportService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/kpi")
    public ResponseEntity<ReportDTO.KpiStats> getKpi() {
        return ResponseEntity.ok(reportService.getKpiStats());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/chart")
    public ResponseEntity<List<ReportDTO.SalesByDate>> getChart() {
        return ResponseEntity.ok(reportService.getSalesChart());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-products")
    public ResponseEntity<List<ReportDTO.TopProduct>> getTopProducts() {
        return ResponseEntity.ok(reportService.getTopProducts());
    }
}