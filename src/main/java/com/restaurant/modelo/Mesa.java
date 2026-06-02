package com.restaurant.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mesas")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_mesa", nullable = false, unique = true)
    private Integer numeroDeMesa;

    @Column(nullable = false)
    private Integer capacidad = 4;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMesa estado = EstadoMesa.DISPONIBLE;

    @Column(name = "codigo_qr")
    private String codigoQr;

    @Column(name = "token_sesion")
    private String tokenSesion;

    @Column(name = "token_expira_en")
    private LocalDateTime tokenExpiraEn;
}
