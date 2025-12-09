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
        return productRepository.findAll();
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

        if (request.getInitialBatch() != null) {
            addBatchToProduct(savedProduct.getId(), request.getInitialBatch());
        }

        return savedProduct;
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
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
        // Gracias a @SQLDelete en la entidad, esto hace un borrado lógico
        productRepository.deleteById(id);
    }

    @Transactional
    public Batch addBatchToProduct(Long productId, ProductRequest.BatchRequest batchRequest) {
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