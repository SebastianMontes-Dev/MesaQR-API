package com.restaurant.controlador;

import com.restaurant.dto.RespuestaMesa;
import com.restaurant.dto.SolicitudCrearMesa;
import com.restaurant.servicio.ServicioMesa;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
            @RequestBody SolicitudCrearMesa solicitud,
            HttpServletRequest req) {

        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
        return ResponseEntity.ok(servicioMesa.crearMesa(solicitud, baseUrl));
    }

    @GetMapping
    public ResponseEntity<List<RespuestaMesa>> obtenerTodasLasMesas() {
        return ResponseEntity.ok(servicioMesa.obtenerTodasLasMesas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespuestaMesa> obtenerMesa(@PathVariable Long id) {
        var mesa = servicioMesa.buscarPorId(id);
        return ResponseEntity.ok(RespuestaMesa.builder()
                .id(mesa.getId())
                .numeroDeMesa(mesa.getNumeroDeMesa())
                .capacidad(mesa.getCapacidad())
                .estado(mesa.getEstado().name())
                .urlQr(mesa.getCodigoQr())
                .build());
    }
}
