package com.restaurant.dto.eventos;

import com.restaurant.modelo.EstadoMesa;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventoCambioEstadoMesa extends EventoMesa {
    private EstadoMesa nuevoEstado;

    public EventoCambioEstadoMesa() {
        super();
    }

    public EventoCambioEstadoMesa(Long mesaId, Integer numeroDeMesa, EstadoMesa nuevoEstado) {
        super(mesaId, numeroDeMesa);
        this.nuevoEstado = nuevoEstado;
    }
}
