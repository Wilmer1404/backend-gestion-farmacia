package com.farmasystem.backend.controller;

import com.farmasystem.backend.dto.ExternalApiUser;
import com.farmasystem.backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{docNumber}")
    public ResponseEntity<?> getCustomer(@PathVariable String docNumber) {
        ExternalApiUser customer = customerService.findCustomerByDoc(docNumber);
        
        if (customer == null || customer.getFullName() == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Devolvemos JSON limpio al frontend
        return ResponseEntity.ok(Map.of(
            "nombre", customer.getFullName(),
            "direccion", customer.getDireccion() != null ? customer.getDireccion() : "",
            "documento", customer.getNumeroDocumento()
        ));
    }
}