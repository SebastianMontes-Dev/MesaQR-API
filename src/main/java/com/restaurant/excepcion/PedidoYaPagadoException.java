package com.restaurant.excepcion;

public class PedidoYaPagadoException extends RuntimeException {
    public PedidoYaPagadoException(String mensaje) {
        super(mensaje);
    }
}
