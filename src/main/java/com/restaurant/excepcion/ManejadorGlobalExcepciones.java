package com.restaurant.excepcion;

import com.restaurant.dto.RespuestaError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RespuestaError> manejarNoEncontrado(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new RespuestaError("NO_ENCONTRADO", ex.getMessage()));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<RespuestaError> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new RespuestaError("NO_ENCONTRADO", ex.getMessage()));
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<RespuestaError> manejarForbidden(TokenInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new RespuestaError("ACCESO_DENEGADO", ex.getMessage()));
    }

    @ExceptionHandler(PedidoYaPagadoException.class)
    public ResponseEntity<RespuestaError> manejarConflicto(PedidoYaPagadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new RespuestaError("CONFLICTO", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RespuestaError("VALIDACION", mensaje));
    }

    @ExceptionHandler(QrException.class)
    public ResponseEntity<RespuestaError> manejarQr(QrException ex) {
        log.error("Error generando QR", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespuestaError("ERROR_QR", ex.getMessage()));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<RespuestaError> manejarNoSoportado(UnsupportedOperationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RespuestaError("NO_SOPORTADO", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaError> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RespuestaError("SOLICITUD_INVALIDA", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarGenerico(Exception ex) {
        log.error("Error inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespuestaError("ERROR_INTERNO", "Algo salió mal. Intenta de nuevo."));
    }
}
