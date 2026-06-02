package com.restaurant.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DetalleItemDTO {
    private Long id;
    private String nombrePlatillo;
    private Integer cantidad;
    private BigDecimal precio;
    private BigDecimal subtotal;
    private String notas;
}
