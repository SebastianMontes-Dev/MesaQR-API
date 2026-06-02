package com.restaurant.controlador;

import com.restaurant.dto.ResumenPedidoDTO;
import com.restaurant.dto.SolicitudAgregarItem;
import com.restaurant.servicio.ServicioMesa;
import com.restaurant.servicio.ServicioPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class ControladorPedido {

    private final ServicioPedido servicioPedido;
    private final ServicioMesa servicioMesa;

    @GetMapping("/mesa/{mesaId}")
    public ResponseEntity<ResumenPedidoDTO> obtenerResumen(
            @PathVariable Long mesaId,
            @RequestHeader(value = "X-Session-Token", required = false) String token) {

        servicioMesa.validarToken(mesaId, token);
        return ResponseEntity.ok(servicioPedido.obtenerResumenPedido(mesaId));
    }

    @PostMapping("/mesa/{mesaId}/items")
    public ResponseEntity<Void> agregarItem(
            @PathVariable Long mesaId,
            @RequestHeader("X-Session-Token") String token,
            @RequestBody SolicitudAgregarItem solicitud) {

        servicioMesa.validarToken(mesaId, token);
        servicioPedido.agregarItem(mesaId, solicitud.getPlatilloId(), solicitud.getCantidad(), solicitud.getNotas());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mesa/{mesaId}")
    public ResponseEntity<Void> crearPedido(
            @PathVariable Long mesaId,
            @RequestHeader("X-Session-Token") String token) {

        servicioMesa.validarToken(mesaId, token);
        servicioPedido.crearPedidoParaMesa(mesaId);
        return ResponseEntity.ok().build();
    }
}
