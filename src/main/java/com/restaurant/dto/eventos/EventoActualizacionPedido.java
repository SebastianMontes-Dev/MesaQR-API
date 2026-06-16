package com.restaurant.dto.eventos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventoActualizacionPedido extends EventoMesa {
    private BigDecimal total;
    private int cantidadElementos;
    private Long pedidoId;

    public EventoActualizacionPedido() {
        super();
    }

    public EventoActualizacionPedido(Long mesaId, Integer numeroDeMesa, Long pedidoId, BigDecimal total, int cantidadElementos) {
        super(mesaId, numeroDeMesa);
        this.pedidoId = pedidoId;
        this.total = total;
        this.cantidadElementos = cantidadElementos;
    }
}
