package com.restaurant.servicio;

import com.restaurant.modelo.Platillo;
import com.restaurant.repositorio.PlatilloRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioPlatillo {

    private final PlatilloRepositorio platilloRepositorio;

    @Transactional(readOnly = true)
    public List<Platillo> obtenerPlatillosDisponibles() {
        return platilloRepositorio.findAllByDisponibleTrue();
    }

    public List<Platillo> obtenerPlatillosPorCategoria(String categoria) {
        return platilloRepositorio.findByCategoria(categoria);
    }

    public Platillo buscarPorId(Long id) {
        return platilloRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Platillo no encontrado: " + id));
    }
}
