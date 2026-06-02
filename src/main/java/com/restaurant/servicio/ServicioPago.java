package com.restaurant.servicio;

import com.restaurant.dto.RespuestaPagoDTO;
import com.restaurant.dto.SolicitudPagoDTO;
import com.restaurant.modelo.EstadoPago;
import com.restaurant.modelo.Pago;
import com.restaurant.modelo.Pedido;
import com.restaurant.repositorio.PagoRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicioPago {

    private final PagoRepositorio pagoRepositorio;
    private final ServicioPedido servicioPedido;

    @Transactional
    public RespuestaPagoDTO procesarPago(Long mesaId, SolicitudPagoDTO solicitud) {
        Pedido pedido = servicioPedido.obtenerPedidoActivo(mesaId);
        BigDecimal total = servicioPedido.obtenerTotalPedido(pedido.getId());

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodo(solicitud.getMetodo());
        pago.setMonto(total);

        return switch (solicitud.getMetodo()) {
            case EFECTIVO -> procesarPagoEfectivo(pago, pedido);
            case TARJETA -> procesarPagoTarjeta(pago, pedido, solicitud.getTokenProveedor());
            case TRANSFERENCIA_QR -> procesarPagoTransferenciaQR(pago, pedido);
        };
    }

    private RespuestaPagoDTO procesarPagoEfectivo(Pago pago, Pedido pedido) {
        pago.setEstado(EstadoPago.COMPLETADO);
        pagoRepositorio.save(pago);

        servicioPedido.marcarComoPagado(pedido.getMesa().getId());

        log.info("Pago en efectivo completado: mesa {} - ${}",
                pedido.getMesa().getNumeroDeMesa(), pago.getMonto());

        return RespuestaPagoDTO.builder()
                .pagoId(pago.getId())
                .estado(EstadoPago.COMPLETADO)
                .monto(pago.getMonto())
                .mensaje("Pago en efectivo registrado")
                .build();
    }

    private RespuestaPagoDTO procesarPagoTarjeta(Pago pago, Pedido pedido, String tokenProveedor) {
        pago.setReferenciaProveedor(tokenProveedor);
        pago.setEstado(EstadoPago.COMPLETADO);
        pagoRepositorio.save(pago);

        servicioPedido.marcarComoPagado(pedido.getMesa().getId());

        log.info("Pago con tarjeta procesado: mesa {} - ${}",
                pedido.getMesa().getNumeroDeMesa(), pago.getMonto());

        return RespuestaPagoDTO.builder()
                .pagoId(pago.getId())
                .estado(EstadoPago.COMPLETADO)
                .monto(pago.getMonto())
                .mensaje("Pago con tarjeta procesado")
                .build();
    }

    private RespuestaPagoDTO procesarPagoTransferenciaQR(Pago pago, Pedido pedido) {
        pago.setEstado(EstadoPago.PENDIENTE);
        pagoRepositorio.save(pago);

        return RespuestaPagoDTO.builder()
                .pagoId(pago.getId())
                .estado(EstadoPago.PENDIENTE)
                .monto(pago.getMonto())
                .mensaje("Realiza la transferencia QR")
                .urlRedireccion("/api/pagos/" + pago.getId() + "/confirmar")
                .build();
    }

    @Transactional
    public void manejarWebhook(String payload, String firma) {
        log.info("Webhook recibido: firma={}", firma);
        log.debug("Payload: {}", payload);
    }

    @Transactional
    public RespuestaPagoDTO confirmarPagoQR(Long pagoId) {
        Pago pago = pagoRepositorio.findById(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + pagoId));

        pago.setEstado(EstadoPago.COMPLETADO);
        pagoRepositorio.save(pago);

        servicioPedido.marcarComoPagado(pago.getPedido().getMesa().getId());

        log.info("Pago QR confirmado: ${}", pago.getMonto());

        return RespuestaPagoDTO.builder()
                .pagoId(pago.getId())
                .estado(EstadoPago.COMPLETADO)
                .monto(pago.getMonto())
                .mensaje("Pago confirmado exitosamente")
                .build();
    }
}
