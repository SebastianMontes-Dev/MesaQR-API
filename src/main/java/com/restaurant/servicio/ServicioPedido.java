package com.restaurant.servicio;

import com.restaurant.dto.DetalleElementoDTO;
import com.restaurant.dto.ResumenPedidoDTO;
import com.restaurant.dto.eventos.EventoActualizacionPedido;
import com.restaurant.dto.eventos.EventoCambioEstadoMesa;
import com.restaurant.modelo.*;
import com.restaurant.excepcion.PedidoYaPagadoException;
import com.restaurant.excepcion.RecursoNoEncontradoException;
import com.restaurant.repositorio.PedidoRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicioPedido {

    private final PedidoRepositorio pedidoRepositorio;
    private final ServicioPlatillo servicioPlatillo;
    private final ServicioMesa servicioMesa;
    private final SimpMessagingTemplate mensajeria;

    @Transactional
    public ResumenPedidoDTO obtenerResumenPedido(Long mesaId) {
        Pedido pedido = obtenerPedidoActivo(mesaId);
        return construirResumen(pedido);
    }

    @Transactional
    @Retryable(retryFor = {PessimisticLockingFailureException.class, CannotAcquireLockException.class},
               maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void agregarElemento(Long mesaId, Long platilloId, int cantidad, String notas) {
        Pedido pedido = obtenerPedidoActivo(mesaId);

        Platillo platillo = servicioPlatillo.buscarPorId(platilloId);

        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setPlatillo(platillo);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(platillo.getPrecio());
        detalle.setNotas(notas);

        pedido.getDetalles().add(detalle);
        pedidoRepositorio.save(pedido);

        Mesa mesa = pedido.getMesa();
        BigDecimal total = obtenerTotalPedido(pedido.getId());

        EventoActualizacionPedido evento = new EventoActualizacionPedido(
                mesa.getId(), mesa.getNumeroDeMesa(), pedido.getId(), total, pedido.getDetalles().size()
        );
        mensajeria.convertAndSend("/topic/mesas", evento);

        log.info("Elemento agregado a mesa {}: {}x {} - total: {}",
                mesa.getNumeroDeMesa(), cantidad, platillo.getNombre(), total);
    }

    @Transactional
    public Pedido crearPedidoParaMesa(Long mesaId) {
        Optional<Pedido> existente = pedidoRepositorio.findActivoByMesaId(mesaId);
        if (existente.isPresent()) {
            return existente.get();
        }

        Mesa mesa = servicioMesa.buscarPorId(mesaId);

        if (mesa.getEstado() == EstadoMesa.DISPONIBLE) {
            servicioMesa.actualizarEstado(mesaId, EstadoMesa.OCUPADA);
        }

        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setEstado(EstadoPedido.ABIERTO);
        pedido.setCreadoEn(LocalDateTime.now());

        return pedidoRepositorio.save(pedido);
    }

    @Transactional
    public Pedido marcarComoPagado(Long mesaId) {
        Pedido pedido = obtenerPedidoActivo(mesaId);
        pedido.setEstado(EstadoPedido.PAGADO);
        pedido.setPagadoEn(LocalDateTime.now());
        pedidoRepositorio.save(pedido);

        servicioMesa.actualizarEstado(mesaId, EstadoMesa.DISPONIBLE);

        EventoCambioEstadoMesa evento = new EventoCambioEstadoMesa(
                mesaId, pedido.getMesa().getNumeroDeMesa(), EstadoMesa.DISPONIBLE
        );
        mensajeria.convertAndSend("/topic/mesas", evento);

        log.info("Pedido pagado: mesa {} - total: {}",
                pedido.getMesa().getNumeroDeMesa(), obtenerTotalPedido(pedido.getId()));
        return pedido;
    }

    public Pedido obtenerPedidoActivo(Long mesaId) {
        return pedidoRepositorio.findActivoByMesaId(mesaId)
                .orElseThrow(() -> new PedidoYaPagadoException("El pedido de la mesa " + mesaId + " ya fue pagado o no existe"));
    }

    public BigDecimal obtenerTotalPedido(Long pedidoId) {
        return pedidoRepositorio.getTotalPedido(pedidoId);
    }

    private ResumenPedidoDTO construirResumen(Pedido pedido) {
        List<DetalleElementoDTO> detalles = pedido.getDetalles().stream()
                .map(d -> DetalleElementoDTO.builder()
                        .id(d.getId())
                        .nombrePlatillo(d.getPlatillo().getNombre())
                        .cantidad(d.getCantidad())
                        .precio(d.getPrecio())
                        .subTotal(d.getPrecio().multiply(BigDecimal.valueOf(d.getCantidad())))
                        .notas(d.getNotas())
                        .build())
                .toList();

        BigDecimal total = detalles.stream()
                .map(DetalleElementoDTO::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResumenPedidoDTO.builder()
                .pedidoId(pedido.getId())
                .numeroDeMesa(pedido.getMesa().getNumeroDeMesa())
                .detalles(detalles)
                .total(total)
                .estado(pedido.getEstado())
                .build();
    }
}
