package com.restaurant.repositorio;

import com.restaurant.modelo.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoRepositorio extends JpaRepository<DetallePedido, Long> {
}
