package com.restaurant.dto.eventos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
    @JsonSubTypes.Type(value = EventoActualizacionPedido.class, name = "ACTUALIZACION_PEDIDO"),
    @JsonSubTypes.Type(value = EventoCambioEstadoMesa.class, name = "CAMBIO_ESTADO")
})
public abstract class EventoMesa {
    private Long mesaId;
    private Integer numeroDeMesa;

    protected EventoMesa(Long mesaId, Integer numeroDeMesa) {
        this.mesaId = mesaId;
        this.numeroDeMesa = numeroDeMesa;
    }
}
