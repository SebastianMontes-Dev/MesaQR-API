package com.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RespuestaError {
    private String codigo;
    private String mensaje;
}
