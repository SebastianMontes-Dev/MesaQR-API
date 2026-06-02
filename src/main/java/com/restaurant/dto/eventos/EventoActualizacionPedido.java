package com.restaurant.dto.eventos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventoActualizacionPedido extends EventoMesa {
    private BigDecimal total;
    private int cantidadItems;
    private Long pedidoId;

    public EventoActualizacionPedido() {
        super();
    }

    public EventoActualizacionPedido(Long mesaId, Integer numeroDeMesa, Long pedidoId, BigDecimal total, int cantidadItems) {
        super(mesaId, numeroDeMesa);
        this.pedidoId = pedidoId;
        this.total = total;
        this.cantidadItems = cantidadItems;
    }
}
