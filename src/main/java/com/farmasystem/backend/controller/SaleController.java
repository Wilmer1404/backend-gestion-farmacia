package com.farmasystem.backend.controller;

import com.farmasystem.backend.dto.SaleRequest;
import com.farmasystem.backend.model.Sale;
import com.farmasystem.backend.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") 
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<Sale> processSale(@RequestBody SaleRequest request) {
        Sale newSale = saleService.createSale(request);
        return ResponseEntity.ok(newSale);
    }
}