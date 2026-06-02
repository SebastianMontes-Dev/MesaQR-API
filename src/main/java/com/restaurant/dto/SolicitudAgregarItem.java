package com.restaurant.dto;

import lombok.Data;

@Data
public class SolicitudAgregarItem {
    private Long platilloId;
    private Integer cantidad = 1;
    private String notas;
}
