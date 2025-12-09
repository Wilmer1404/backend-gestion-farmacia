package com.farmasystem.backend.service;

import com.farmasystem.backend.dto.ProductRequest;
import com.farmasystem.backend.model.Batch;
import com.farmasystem.backend.model.Product;
import com.farmasystem.backend.repository.BatchRepository;
import com.farmasystem.backend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;

    public List<Product> getAllProducts() {
        // Usamos la consulta optimizada para evitar N+1 queries
        return productRepository.findAllWithBatches();
    }

    @Transactional
    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setSalePrice(request.getSalePrice());
        product.setMinStock(request.getMinStock());
        product.setProvider(request.getProvider());
        product.setCreatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        // CORRECCIÓN 1: Validamos que el ID no sea nulo Y que exista un lote inicial antes de procesarlo
        // Esto evita el NullPointerException si creas un producto sin stock inicial
        if (savedProduct.getId() != null && request.getInitialBatch() != null) {
            addBatchToProduct(savedProduct.getId(), request.getInitialBatch());
        }

        return savedProduct;
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        // CORRECCIÓN 2: Validación de seguridad
        if (id == null) throw new IllegalArgumentException("El ID del producto es obligatorio");

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setSalePrice(request.getSalePrice());
        product.setMinStock(request.getMinStock());
        product.setProvider(request.getProvider());

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (id == null) throw new IllegalArgumentException("El ID del producto es obligatorio");
        productRepository.deleteById(id);
    }

    @Transactional
    public Batch addBatchToProduct(Long productId, ProductRequest.BatchRequest batchRequest) {
        // CORRECCIÓN 3: Validación de seguridad
        if (productId == null) throw new IllegalArgumentException("El ID del producto es obligatorio");

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