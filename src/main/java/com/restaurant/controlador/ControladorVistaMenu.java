package com.restaurant.controlador;

import com.restaurant.dto.ResumenPedidoDTO;
import com.restaurant.excepcion.PedidoYaPagadoException;
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
                            @RequestParam(required = false) String token,
                            @CookieValue(value = "session_token", required = false) String cookieToken,
                            Model modelo) {

        if (token == null && cookieToken != null) {
            token = cookieToken;
        }

        modelo.addAttribute("mesaId", mesaId);
        modelo.addAttribute("nombreRestaurante", nombreRestaurante);
        modelo.addAttribute("platillos", servicioPlatillo.obtenerPlatillosDisponibles());

        if (token == null || token.isEmpty()) {
            modelo.addAttribute("tokenFaltante", true);
            return "menu";
        }

        servicioMesa.validarToken(mesaId, token);

        ResumenPedidoDTO resumen;
        try {
            resumen = servicioPedido.obtenerResumenPedido(mesaId);
        } catch (PedidoYaPagadoException e) {
            modelo.addAttribute("token", token);
            modelo.addAttribute("pedidoPagado", true);
            modelo.addAttribute("mensajeError", e.getMessage());
            return "menu";
        } catch (RuntimeException e) {
            servicioPedido.crearPedidoParaMesa(mesaId);
            resumen = servicioPedido.obtenerResumenPedido(mesaId);
        }

        modelo.addAttribute("token", token);
        modelo.addAttribute("resumen", resumen);

        return "menu";
    }
}
