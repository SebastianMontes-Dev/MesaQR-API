package com.restaurant.controlador;

import com.restaurant.servicio.ServicioMesa;
import com.restaurant.servicio.ServicioQR;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ControladorQR {

    private final ServicioQR servicioQR;
    private final ServicioMesa servicioMesa;

    @GetMapping("/mesas/{id}/qr")
    public ResponseEntity<byte[]> obtenerQR(@PathVariable Long id, HttpServletRequest req) throws Exception {
        var mesa = servicioMesa.buscarPorId(id);
        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
        byte[] qr = servicioQR.generarQR(mesa.getId(), baseUrl, mesa.getTokenSesion());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qr);
    }
}
