package com.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudAgregarElemento {
    @NotNull(message = "El ID del platillo es obligatorio")
    private Long platilloId;

    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad = 1;

    private String notas;
}
