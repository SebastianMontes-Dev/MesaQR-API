package com.restaurant.servicio;

import com.restaurant.dto.RespuestaMesa;
import com.restaurant.dto.SolicitudCrearMesa;
import com.restaurant.excepcion.RecursoNoEncontradoException;
import com.restaurant.excepcion.TokenInvalidoException;
import com.restaurant.modelo.EstadoMesa;
import com.restaurant.modelo.Mesa;
import com.restaurant.repositorio.MesaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicioMesa {

    private final MesaRepositorio mesaRepositorio;

    @Transactional
    public RespuestaMesa crearMesa(SolicitudCrearMesa solicitud, String baseUrl) {
        if (mesaRepositorio.existsByNumeroDeMesa(solicitud.getNumeroDeMesa())) {
            throw new IllegalArgumentException("El número de mesa ya existe: " + solicitud.getNumeroDeMesa());
        }

        String token = UUID.randomUUID().toString();

        Mesa mesa = new Mesa();
        mesa.setNumeroDeMesa(solicitud.getNumeroDeMesa());
        mesa.setCapacidad(solicitud.getCapacidad() != null ? solicitud.getCapacidad() : 4);
        mesa.setEstado(EstadoMesa.DISPONIBLE);
        mesa.setTokenSesion(token);
        mesa.setTokenExpiraEn(LocalDateTime.now().plusHours(24));

        mesaRepositorio.save(mesa);

        return toRespuesta(mesa, baseUrl);
    }

    public List<RespuestaMesa> obtenerTodasLasMesas() {
        return mesaRepositorio.findAll().stream()
                .map(m -> toRespuesta(m, null))
                .toList();
    }

    public Mesa buscarPorId(Long mesaId) {
        return mesaRepositorio.findById(mesaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada: " + mesaId));
    }

    @Transactional
    public void validarToken(Long mesaId, String token) {
        Mesa mesa = mesaRepositorio.findByIdConBloqueo(mesaId)
                .orElseThrow(() -> new TokenInvalidoException("Mesa no encontrada"));

        if (mesa.getTokenSesion() == null || !mesa.getTokenSesion().equals(token)) {
            throw new TokenInvalidoException("Token de sesión inválido para la mesa " + mesa.getNumeroDeMesa());
        }

        if (mesa.getTokenExpiraEn() != null && mesa.getTokenExpiraEn().isBefore(LocalDateTime.now())) {
            throw new TokenInvalidoException("El token de sesión expiró para la mesa " + mesa.getNumeroDeMesa());
        }
    }

    @Transactional
    public void actualizarEstado(Long mesaId, EstadoMesa nuevoEstado) {
        Mesa mesa = mesaRepositorio.findByIdConBloqueo(mesaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada: " + mesaId));
        mesa.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoMesa.DISPONIBLE) {
            regenerarToken(mesa);
        }

        mesaRepositorio.save(mesa);
    }

    @Transactional
    public void regenerarToken(Mesa mesa) {
        mesa.setTokenSesion(UUID.randomUUID().toString());
        mesa.setTokenExpiraEn(LocalDateTime.now().plusHours(24));
    }

    @Transactional(readOnly = true)
    public boolean existeTokenValido(String token) {
        return mesaRepositorio.findByTokenSesion(token)
                .map(m -> m.getTokenExpiraEn() == null || m.getTokenExpiraEn().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    private RespuestaMesa toRespuesta(Mesa mesa, String baseUrl) {
        String urlQr = baseUrl != null ? baseUrl + "/api/mesas/" + mesa.getId() + "/qr" : null;
        return RespuestaMesa.builder()
                .id(mesa.getId())
                .numeroDeMesa(mesa.getNumeroDeMesa())
                .capacidad(mesa.getCapacidad())
                .estado(mesa.getEstado().name())
                .urlQr(urlQr)
                .build();
    }
}
