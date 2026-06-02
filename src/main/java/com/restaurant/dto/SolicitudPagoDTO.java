package com.restaurant.dto;

import com.restaurant.modelo.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudPagoDTO {
    @NotNull
    private MetodoPago metodo;
    private String tokenProveedor;
}
