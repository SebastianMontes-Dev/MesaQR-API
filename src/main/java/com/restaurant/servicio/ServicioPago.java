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

import com.restaurant.excepcion.RecursoNoEncontradoException;
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
            default -> throw new UnsupportedOperationException("Metodo de pago no soportado: " + solicitud.getMetodo());
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

    @org.springframework.beans.factory.annotation.Value("${restaurant.webhook.secret:secreto_default}")
    private String webhookSecret;

    @Transactional
    public void manejarNotificacionExterna(String payload, String firma) {
        log.info("Notificación externa recibida: firma={}", firma);
        
        if (firma == null || firma.isEmpty()) {
            throw new IllegalArgumentException("Firma de webhook faltante");
        }

        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(webhookSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hmacBytes) {
                sb.append(String.format("%02x", b));
            }
            String firmaCalculada = sb.toString();

            if (!firmaCalculada.equalsIgnoreCase(firma)) {
                log.error("Firma de webhook inválida. Calculada: {}, Recibida: {}", firmaCalculada, firma);
                throw new IllegalArgumentException("Firma de webhook inválida");
            }
        } catch (java.security.NoSuchAlgorithmException | java.security.InvalidKeyException e) {
            throw new RuntimeException("Error al calcular HMAC", e);
        }

        log.debug("Payload validado exitosamente: {}", payload);
    }

    @Transactional
    public RespuestaPagoDTO confirmarPagoQR(Long pagoId) {
        Pago pago = pagoRepositorio.findById(pagoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado: " + pagoId));

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
