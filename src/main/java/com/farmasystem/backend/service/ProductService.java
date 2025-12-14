package com.farmasystem.backend.service;

import com.farmasystem.backend.dto.ProductRequest;
import com.farmasystem.backend.model.Batch;
import com.farmasystem.backend.model.Product;
import com.farmasystem.backend.repository.BatchRepository;
import com.farmasystem.backend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAllWithBatches();
    }

    @Transactional
    public Product createProduct(ProductRequest request) {
        // --- VALIDACIONES DE NEGOCIO ---

        // 1. Validar Rentabilidad (Precio Venta > Precio Compra)
        if (request.getInitialBatch() != null) {
            BigDecimal precioCompra = request.getInitialBatch().getPurchasePrice();
            BigDecimal precioVenta = request.getSalePrice();

            // compareTo devuelve: -1 si es menor, 0 si es igual, 1 si es mayor
            // Si precioCompra >= precioVenta -> Error (No hay ganancia)
            if (precioCompra.compareTo(precioVenta) >= 0) {
                throw new IllegalArgumentException("Error de Rentabilidad: El precio de venta (S/ " + precioVenta + 
                    ") debe ser mayor al costo de compra (S/ " + precioCompra + ").");
            }
        }

        // 2. Validar SKU único (Evitar duplicados)
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("El SKU '" + request.getSku() + "' ya existe en el sistema.");
        }

        // 3. Validar Código de Barras único (si se envía)
        if (request.getBarcode() != null && !request.getBarcode().isBlank() && 
            productRepository.existsByBarcode(request.getBarcode())) {
            throw new IllegalArgumentException("El Código de Barras '" + request.getBarcode() + "' ya está registrado.");
        }

        // --- CREACIÓN DEL PRODUCTO ---
        Product product = new Product();
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setSalePrice(request.getSalePrice());
        product.setMinStock(request.getMinStock());
        product.setProvider(request.getProvider());
        product.setCreatedAt(LocalDateTime.now());
        product.setDeleted(false);

        Product savedProduct = productRepository.save(product);

        if (savedProduct.getId() != null && request.getInitialBatch() != null) {
            addBatchToProduct(savedProduct.getId(), request.getInitialBatch());
        }

        // Devolvemos el producto recargado con sus lotes para evitar errores de Lazy Loading
        return productRepository.findByIdWithBatches(savedProduct.getId())
                .orElse(savedProduct);
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        if (id == null) throw new IllegalArgumentException("El ID es obligatorio");

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Validar unicidad de SKU solo si ha cambiado
        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
             throw new IllegalArgumentException("El SKU '" + request.getSku() + "' ya está en uso por otro producto.");
        }

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setSalePrice(request.getSalePrice());
        product.setMinStock(request.getMinStock());
        product.setProvider(request.getProvider());

        Product updated = productRepository.save(product);

        return productRepository.findByIdWithBatches(updated.getId())
                .orElse(updated);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (id == null) throw new IllegalArgumentException("El ID es obligatorio");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        // Borrado lógico (Soft Delete)
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Transactional
    public Batch addBatchToProduct(Long productId, ProductRequest.BatchRequest batchRequest) {
        if (productId == null) throw new IllegalArgumentException("El ID es obligatorio");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        Batch batch = new Batch();
        batch.setBatchCode(batchRequest.getBatchCode());
        batch.setExpirationDate(batchRequest.getExpirationDate());
        batch.setStock(batchRequest.getStock());
        batch.setPurchasePrice(batchRequest.getPurchasePrice());
        batch.setProduct(product);
        batch.setCreatedAt(LocalDateTime.now());
        
        return batchRepository.save(batch);
    }
}