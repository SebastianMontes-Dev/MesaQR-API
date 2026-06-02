package com.restaurant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespuestaMesa {
    private Long id;
    private Integer numeroDeMesa;
    private Integer capacidad;
    private String estado;
    private String urlQr;
}
