package com.farmasystem.backend.controller;

import com.farmasystem.backend.dto.ProductRequest;
import com.farmasystem.backend.model.Product;
import com.farmasystem.backend.service.ProductService;
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

    // Obtener todos los productos (Para la tabla de inventario)
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Crear un nuevo producto
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    // Agregar stock (nuevo lote) a un producto existente
    @PostMapping("/{id}/batches")
    public ResponseEntity<?> addBatch(@PathVariable Long id, @RequestBody ProductRequest.BatchRequest batchRequest) {
        return ResponseEntity.ok(productService.addBatchToProduct(id, batchRequest));
    }
}