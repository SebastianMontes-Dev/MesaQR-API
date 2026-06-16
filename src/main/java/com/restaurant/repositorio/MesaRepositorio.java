package com.restaurant.repositorio;

import com.restaurant.modelo.EstadoMesa;
import com.restaurant.modelo.Mesa;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MesaRepositorio extends JpaRepository<Mesa, Long> {

    Optional<Mesa> findByNumeroDeMesa(Integer numeroDeMesa);

    List<Mesa> findAllByEstado(EstadoMesa estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Mesa m WHERE m.id = :id")
    Optional<Mesa> findByIdConBloqueo(@Param("id") Long id);

    boolean existsByNumeroDeMesa(Integer numeroDeMesa);

    Optional<Mesa> findByTokenSesion(String tokenSesion);
}
