package com.farmasystem.backend.service;

import com.farmasystem.backend.dto.ExternalApiUser;
import com.farmasystem.backend.model.Sale;
import com.farmasystem.backend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final SaleRepository saleRepository;
    private final RestTemplate restTemplate;

    @Value("${api.token}") 
    private String apiToken;

    public ExternalApiUser findCustomerByDoc(String docNumber) {
        ExternalApiUser customer = new ExternalApiUser();
        customer.setNumeroDocumento(docNumber);

        // 1. ESTRATEGIA LOCAL: Buscar en historial de ventas
        Optional<Sale> lastSale = saleRepository.findFirstByClientDocOrderByCreatedAtDesc(docNumber);
        
        if (lastSale.isPresent() && lastSale.get().getClientName() != null) {
            System.out.println("✅ Cliente encontrado en BD local: " + lastSale.get().getClientName());
            customer.setNombre(lastSale.get().getClientName());
            return customer;
        }

        // 2. ESTRATEGIA EXTERNA: Consultar PeruApi.com
        try {
            String url;
            if (docNumber.length() == 8) {
                url = "https://peruapi.com/api/dni/" + docNumber + "?api_token=" + apiToken;
            } else if (docNumber.length() == 11) {
                url = "https://peruapi.com/api/ruc/" + docNumber + "?api_token=" + apiToken;
            } else {
                return null; // Longitud inválida
            }

            System.out.println(" Consultando API Externa para: " + docNumber);
            ExternalApiUser response = restTemplate.getForObject(url, ExternalApiUser.class);
            
            // Validación extra: a veces la API responde 200 OK pero con campos vacíos si no encuentra nada
            if (response != null && (response.getNombre() == null || response.getNombre().isEmpty())) {
                return null;
            }
            
            return response;

        } catch (Exception e) {
            System.err.println(" Error consultando API: " + e.getMessage());
            return null;
        }
    }
}