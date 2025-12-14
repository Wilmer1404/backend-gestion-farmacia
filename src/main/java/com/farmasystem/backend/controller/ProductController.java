package com.farmasystem.backend.controller;

import com.farmasystem.backend.dto.ProductRequest;
import com.farmasystem.backend.model.Product;
import com.farmasystem.backend.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    // --- NUEVO: IMPORTACIÓN MASIVA (Recibe lista de productos) ---
    @PostMapping("/import")
    public ResponseEntity<List<Product>> importProducts(@RequestBody List<ProductRequest> requests) {
        // En un sistema real, esto iría en un servicio @Transactional batch
        List<Product> saved = requests.stream()
                .map(productService::createProduct)
                .toList();
        return ResponseEntity.ok(saved);
    }

    // --- NUEVO: EDITAR ---
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // --- NUEVO: ELIMINAR (Soft Delete) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/batches")
    public ResponseEntity<?> addBatch(@PathVariable Long id, @RequestBody ProductRequest.BatchRequest batchRequest) {
        return ResponseEntity.ok(productService.addBatchToProduct(id, batchRequest));
    }
}