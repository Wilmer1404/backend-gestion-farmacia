package com.farmasystem.backend.service;

import com.farmasystem.backend.dto.SaleRequest;
import com.farmasystem.backend.model.*;
import com.farmasystem.backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor; 
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor 
public class SaleService {

    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;
    private final SaleRepository saleRepository;

    @Transactional
    public Sale createSale(SaleRequest request) {
        Sale sale = new Sale();
        sale.setCreatedAt(LocalDateTime.now());
        sale.setClientDoc(request.getClientDni());
        
        BigDecimal totalSaleAmount = BigDecimal.ZERO;
        List<SaleDetail> details = new ArrayList<>();

        for (SaleRequest.SaleItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + item.getProductId()));

            List<Batch> batches = batchRepository.findAvailableBatches(item.getProductId());
            
            int quantityNeeded = item.getQuantity();
            int stockAvailable = batches.stream().mapToInt(Batch::getStock).sum();

            if (stockAvailable < quantityNeeded) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
            }

            for (Batch batch : batches) {
                if (quantityNeeded <= 0) break;

                int takeFromBatch = Math.min(batch.getStock(), quantityNeeded);
                
                batch.setStock(batch.getStock() - takeFromBatch);
                batchRepository.save(batch); 

                quantityNeeded -= takeFromBatch;
            }

            SaleDetail detail = new SaleDetail();
            detail.setProduct(product);
            detail.setSale(sale);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(product.getSalePrice());
            detail.setSubtotal(product.getSalePrice().multiply(new BigDecimal(item.getQuantity())));
            
            details.add(detail);
            totalSaleAmount = totalSaleAmount.add(detail.getSubtotal());
        }

        sale.setTotal(totalSaleAmount);
        sale.setDetails(details);
        
        return saleRepository.save(sale);
    }
}