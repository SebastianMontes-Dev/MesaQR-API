package com.restaurant.controlador;

import com.restaurant.dto.RespuestaMesa;
import com.restaurant.dto.SolicitudCrearMesa;
import com.restaurant.servicio.ServicioMesa;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
public class ControladorMesa {

    private final ServicioMesa servicioMesa;

    @PostMapping
    public ResponseEntity<RespuestaMesa> crearMesa(
            @RequestBody @Valid SolicitudCrearMesa solicitud,
            HttpServletRequest req) {

        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioMesa.crearMesa(solicitud, baseUrl));
    }

    @GetMapping
    public ResponseEntity<List<RespuestaMesa>> obtenerTodasLasMesas() {
        return ResponseEntity.ok(servicioMesa.obtenerTodasLasMesas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespuestaMesa> obtenerMesa(@PathVariable Long id, HttpServletRequest req) {
        var mesa = servicioMesa.buscarPorId(id);
        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
        return ResponseEntity.ok(RespuestaMesa.builder()
                .id(mesa.getId())
                .numeroDeMesa(mesa.getNumeroDeMesa())
                .capacidad(mesa.getCapacidad())
                .estado(mesa.getEstado().name())
                .urlQr(baseUrl + "/api/mesas/" + mesa.getId() + "/qr")
                .build());
    }
}
