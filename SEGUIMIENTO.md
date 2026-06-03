# MesaQR — Plan de subida progresiva

Repo: https://github.com/SebastianMontes-Dev/MesaQR
Local: C:\Users\sabas\Documentos\restaurant-manager

## Progreso

| Día | Tarea | Branch | Estado |
|-----|-------|--------|--------|
| 1 | Setup: pom.xml, AplicacionMesaQR, properties, docker-compose | main | ✅ Completado |
| 2 | Enums + Entidades JPA (paquete `modelo`) | main | ✅ Completado |
| 3 | Repositorios con locks pesimistas (paquete `repositorio`) | main | ✅ Completado |
| 4 | DTOs + Eventos WebSocket (paquete `dto`, `dto.eventos`) | main | ✅ Completado |
| 5 | Servicios (paquete `servicio`) | main | ✅ Completado |
| 6 | Controladores + Config + Excepciones (paquetes `controlador`, `configuracion`, `excepcion`) | main | ✅ Completado |
| 7 | Migraciones Flyway (V1-V5) + Thymeleaf menu.html | main | ✅ Completado |
| 8 | Limpieza: eliminación de paquetes duplicados en inglés | main | ✅ Completado |

## Proyecto finalizado

100% completado y subido a https://github.com/SebastianMontes-Dev/MesaQR-API

## Convenciones del proyecto (español)

### Paquetes
| Inglés (antes) | Español (actual) |
|---|---|
| `model.*` | `modelo.*` |
| `repository.*` | `repositorio.*` |
| `service.*` | `servicio.*` |
| `controller.*` | `controlador.*` |
| `exception.*` | `excepcion.*` |
| `config.*` | `configuracion.*` |
| `dto.events.*` | `dto.eventos.*` |

### Entidades y enums
| Inglés (antes) | Español (actual) | Tipo |
|---|---|---|
| `RestaurantManagerApplication` | `AplicacionMesaQR` | Clase principal |
| `RestaurantTable` / `restaurant_tables` | `Mesa` / `mesas` | Entidad |
| `MenuItem` / `menu_items` | `Platillo` / `platillos` | Entidad |
| `Order` / `orders` | `Pedido` / `pedidos` | Entidad |
| `OrderItem` / `order_items` | `DetallePedido` / `detalles_pedido` | Entidad |
| `Payment` / `payments` | `Pago` / `pagos` | Entidad |
| `TableStatus` | `EstadoMesa` (DISPONIBLE, OCUPADA, RESERVADA) | Enum |
| `OrderStatus` | `EstadoPedido` (ABIERTO, PAGADO, CANCELADO) | Enum |
| `PaymentMethod` | `MetodoPago` (EFECTIVO, TARJETA, TRANSFERENCIA_QR) | Enum |
| `PaymentStatus` | `EstadoPago` (PENDIENTE, COMPLETADO, FALLIDO) | Enum |

### DTOs
| Inglés (antes) | Español (actual) |
|---|---|
| `AddItemRequest` | `SolicitudAgregarItem` |
| `ErrorResponse` | `RespuestaError` |
| `OrderItemDTO` | `DetalleItemDTO` |
| `OrderSummaryDTO` | `ResumenPedidoDTO` |
| `PaymentRequestDTO` | `SolicitudPagoDTO` |
| `PaymentResponseDTO` | `RespuestaPagoDTO` |
| `CreateTableRequest` | `SolicitudCrearMesa` |
| `TableResponse` | `RespuestaMesa` |
| `TableEvent` | `EventoMesa` |
| `OrderUpdateEvent` | `EventoActualizacionPedido` |
| `TableStatusEvent` | `EventoCambioEstadoMesa` |

### Servicios y controladores
| Inglés (antes) | Español (actual) |
|---|---|
| `QRService` | `ServicioQR` |
| `TableService` | `ServicioMesa` |
| `OrderService` | `ServicioPedido` |
| `PaymentService` | `ServicioPago` |
| `MenuItemService` | `ServicioPlatillo` |
| `TableController` | `ControladorMesa` |
| `OrderController` | `ControladorPedido` |
| `PaymentController` | `ControladorPago` |
| `QRController` | `ControladorQR` |
| `MenuViewController` | `ControladorVistaMenu` |
| `GlobalExceptionHandler` | `ManejadorGlobalExcepciones` |
| `WebSocketConfig` | `ConfiguracionWebSocket` |
| `InvalidTokenException` | `TokenInvalidoException` |
| `OrderAlreadyPaidException` | `PedidoYaPagadoException` |

### Endpoints API
| Antes | Ahora |
|---|---|
| `/api/tables` | `/api/mesas` |
| `/api/orders` | `/api/pedidos` |
| `/api/payments` | `/api/pagos` |
| `/api/tables/{id}/qr` | `/api/mesas/{id}/qr` |
| `/topic/tables` (WebSocket) | `/topic/mesas` |

## Instrucciones para retomar

Decile al asistente: "subí el día 4 del plan de MesaQR" (o día 5, 6, 7).
Todo el código ya está traducido localmente — solo falta hacer commit y push de cada día.
