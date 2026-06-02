package com.restaurant.dto;

import com.restaurant.modelo.EstadoPago;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RespuestaPagoDTO {
    private Long pagoId;
    private EstadoPago estado;
    private BigDecimal monto;
    private String mensaje;
    private String secretoCliente;
    private String urlRedireccion;
}
