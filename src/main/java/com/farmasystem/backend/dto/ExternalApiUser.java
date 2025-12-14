package com.farmasystem.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class ExternalApiUser {
    
    // Mapea 'cliente', 'razon_social' o 'nombres' al campo 'nombre'
    @JsonAlias({"cliente", "razon_social", "nombres"}) 
    private String nombre; 

    @JsonAlias("apellido_paterno")
    private String apellidoPaterno;

    @JsonAlias("apellido_materno")
    private String apellidoMaterno;

    @JsonAlias("direccion")
    private String direccion;

    // Mapea 'dni' o 'ruc' al campo 'numeroDocumento'
    @JsonAlias({"dni", "ruc", "numero_documento"})
    private String numeroDocumento;
    
    // Une los nombres si vienen separados
    public String getFullName() {
        if (nombre != null && apellidoPaterno == null) return nombre;
        
        return (nombre != null ? nombre : "") + 
               (apellidoPaterno != null ? " " + apellidoPaterno : "") + 
               (apellidoMaterno != null ? " " + apellidoMaterno : "");
    }
}