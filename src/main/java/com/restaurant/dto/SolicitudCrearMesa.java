package com.restaurant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolicitudCrearMesa {
    private Integer numeroDeMesa;
    private Integer capacidad;
}
