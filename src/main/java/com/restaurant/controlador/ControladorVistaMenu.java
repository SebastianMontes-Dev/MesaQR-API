package com.restaurant.controlador;

import com.restaurant.dto.ResumenPedidoDTO;
import com.restaurant.servicio.ServicioMesa;
import com.restaurant.servicio.ServicioPedido;
import com.restaurant.servicio.ServicioPlatillo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/menu")
@RequiredArgsConstructor
public class ControladorVistaMenu {

    private final ServicioPedido servicioPedido;
    private final ServicioMesa servicioMesa;
    private final ServicioPlatillo servicioPlatillo;

    @Value("${restaurant.name:Mi Restaurante}")
    private String nombreRestaurante;

    @GetMapping("/{mesaId}")
    public String vistaMenu(@PathVariable Long mesaId,
                            @RequestParam String token,
                            Model modelo) {

        servicioMesa.validarToken(mesaId, token);

        ResumenPedidoDTO resumen;
        try {
            resumen = servicioPedido.obtenerResumenPedido(mesaId);
        } catch (RuntimeException e) {
            servicioPedido.crearPedidoParaMesa(mesaId);
            resumen = servicioPedido.obtenerResumenPedido(mesaId);
        }

        modelo.addAttribute("mesaId", mesaId);
        modelo.addAttribute("token", token);
        modelo.addAttribute("resumen", resumen);
        modelo.addAttribute("nombreRestaurante", nombreRestaurante);
        modelo.addAttribute("platillos", servicioPlatillo.obtenerPlatillosDisponibles());

        return "menu";
    }
}
