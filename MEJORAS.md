# MEJORAS.md — Plan de mejora MesaQR API

> Auditoría completa del proyecto. 28 puntos de mejora distribuidos en 5 sprints.

---

## Sprint 1 — Seguridad

**Objetivo**: que nadie pueda tomar control ajeno ni espiar datos.

- [ ] 1.1 **Quitar token de la URL del QR** — pasarlo como fragment (`#token=...`) para que el servidor no lo loguee ni el navegador lo comparta. Modificar `ServicioQR.java:15` y `menu.html` para leerlo del hash.
- [ ] 1.2 **Proteger WebSocket** — requerir token vía handshake STOMP. El cliente lo envía como header `X-Session-Token` al conectar, `ConfiguracionWebSocket` lo valida con un `ChannelInterceptor`.
- [ ] 1.3 **Restringir CORS** — cambiar `setAllowedOriginPatterns("*")` por orígenes específicos en `application.properties` (`restaurant.cors.allowed-origins`).
- [ ] 1.4 **Verificar firma del webhook** — implementar HMAC-SHA256 en `ServicioPago.manejarWebhook()` usando un secreto configurable.
- [ ] 1.5 **Externalizar credenciales** — mover `spring.datasource.password` a variable de entorno `${DB_PASSWORD}` con valor default solo para dev.
- [ ] 1.6 **Agregar rate limiting** — usar Bucket4j o Resilience4j con anotación `@RateLimiter` en endpoints de pago y pedido.

---

## Sprint 2 — Lógica de negocio

**Objetivo**: que los flujos no tengan agujeros semánticos.

- [ ] 2.1 **Evitar pedidos duplicados** — agregar validación en `ServicioPedido.crearPedidoParaMesa()` que revise si ya existe un pedido ABIERTO para esa mesa.
- [ ] 2.2 **Confirmación de pago en efectivo** — el pago en efectivo queda `PENDIENTE` igual que QR, y solo el mesero (o admin) lo confirma con un endpoint `/api/pagos/{id}/confirmar-efectivo`.
- [ ] 2.3 **Activar `PedidoYaPagadoException`** — lanzarla en `obtenerPedidoActivo` cuando el pedido existe pero no está ABIERTO.
- [ ] 2.4 **Agregar cálculo de IVA y propina** — sumar 16% IVA o 10% servicio según configuración del restaurante en `application.properties`.
- [ ] 2.5 **Endpoint cancelar pedido** — `PUT /api/pedidos/mesa/{mesaId}/cancelar` que pase el pedido a CANCELADO y libere la mesa.
- [ ] 2.6 **Implementar reservas o eliminar `RESERVADA`** — si se quiere funcional, `PUT /api/mesas/{id}/reservar` + `PUT /api/mesas/{id}/liberar`.

---

## Sprint 3 — Validación y robustez

**Objetivo**: que ningún input inválido llegue a la lógica de negocio.

- [ ] 3.1 **Validar todos los DTOs** — `@NotNull` en `platilloId`, `@Min(1)` en `cantidad`, `@NotNull @Min(1)` en `numeroDeMesa`, `@NotNull` en `capacidad`.
- [ ] 3.2 **Agregar `default` en switch de pagos** — lanzar `UnsupportedOperationException` con mensaje claro si el enum tiene un valor nuevo.
- [ ] 3.3 **Unificar `mesaId` en `SolicitudPagoDTO`** — moverlo del `@PathVariable` al body del DTO para consistencia.
- [ ] 3.4 **Agregar validación de integridad de BD al startup** — Flyway ya lo maneja, pero agregar health indicator con Actuator.

---

## Sprint 4 — Arquitectura y código

**Objetivo**: limpiar deuda técnica y preparar para producción.

- [ ] 4.1 **Reducir doble save en creación de mesa** — generar la URL del QR antes del primer `save`, hacer un solo round-trip.
- [ ] 4.2 **Manejar headers de proxy** — usar `ForwardedHeaderFilter` de Spring para que la URL del QR funcione detrás de Nginx/Traefik.
- [ ] 4.3 **Reemplazar `RuntimeException` por excepciones de dominio** — crear `RecursoNoEncontradoException` y usarla en `buscarPorId`.
- [ ] 4.4 **Externalizar constantes a properties** — tamaño QR, duración token, reintentos, backoff, capacidad default → `application.properties`.
- [ ] 4.5 **Agregar `@Transactional(readOnly = true)`** faltantes en `ServicioPlatillo`.
- [ ] 4.6 **Firmar método `generarQR` sin `throws Exception`** — envolver `ZXingException` en excepción de dominio.

---

## Sprint 5 — Testing, DevOps y observabilidad

**Objetivo**: despliegue profesional con métricas y cobertura.

### Testing
- [ ] 5.1 **Tests unitarios de servicios** — `ServicioPedido`, `ServicioPago`, `ServicioMesa` con Mockito.
- [ ] 5.2 **Tests de integración con Testcontainers** — reemplazar H2 con PostgreSQL real en tests, probar flujo completo mesa→pedido→pago.
- [ ] 5.3 **Tests de concurrencia** — validar que los locks pesimistas funcionan con `ExecutorService` multi-hilo.

### Observabilidad
- [ ] 5.4 **Agregar Spring Actuator** — health checks, métricas, info. Exponer `/actuator/health` y `/actuator/metrics`.
- [ ] 5.5 **Agregar Micrometer + Prometheus** — métricas de latencia por endpoint, tasa de error, throughput.
- [ ] 5.6 **Graceful shutdown** — `server.shutdown=graceful` + `spring.lifecycle.timeout-per-shutdown-phase=30s`.

### DevOps
- [ ] 5.7 **Containerizar la app** — agregar `Dockerfile` y servicio en `docker-compose.yml` con health check y depends_on.
- [ ] 5.8 **Perfiles Spring** — crear `application-dev.yml`, `application-prod.yml` con configs separadas.

### Código
- [ ] 5.9 **Separar CSS y JS de menu.html** — mover estilos a `menu.css` y JS a `menu.js`, cargarlos estáticamente.
- [ ] 5.10 **Javadoc en clases públicas** — mínimo en servicios y DTOs.

---

## Progreso por sprint

| Sprint | Área | Estado | Completado |
|--------|------|--------|------------|
| 1 | Seguridad | ⬜ Pendiente | 0/6 |
| 2 | Lógica de negocio | ⬜ Pendiente | 0/6 |
| 3 | Validación y robustez | ⬜ Pendiente | 0/4 |
| 4 | Arquitectura y código | ⬜ Pendiente | 0/6 |
| 5 | Testing, DevOps y observabilidad | ⬜ Pendiente | 0/10 |

**Total**: 0/28 — 0%
