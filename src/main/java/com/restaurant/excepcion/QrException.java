package com.restaurant.excepcion;

public class QrException extends RuntimeException {
    public QrException(String mensaje) {
        super(mensaje);
    }

    public QrException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
