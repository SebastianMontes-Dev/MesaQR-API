package com.restaurant.repositorio;

import com.restaurant.modelo.Platillo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatilloRepositorio extends JpaRepository<Platillo, Long> {

    List<Platillo> findAllByDisponibleTrue();

    List<Platillo> findByCategoria(String categoria);
}
