package com.restaurant.repositorio;

import com.restaurant.modelo.Pago;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PagoRepositorio extends JpaRepository<Pago, Long> {

    Optional<Pago> findByReferenciaProveedor(String referenciaProveedor);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pago p WHERE p.pedido.id = :pedidoId")
    Optional<Pago> findByPedidoIdConBloqueo(@Param("pedidoId") Long pedidoId);
}
