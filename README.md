# MesaQR API

Sistema de gestión de pedidos y pagos QR para restaurantes con Spring Boot, PostgreSQL, WebSocket y Flyway.

## Stack

- **Java 21** + **Spring Boot 3.3**
- **PostgreSQL 16** + **Flyway** (migraciones)
- **WebSocket** (STOMP sobre SockJS) para notificaciones en tiempo real
- **ZXing** para generación de códigos QR
- **Thymeleaf** para vista del menú

## Arranque rápido

### 1. Base de datos

```bash
docker-compose up -d
```

### 2. Aplicación

```bash
./mvnw spring-boot:run
```

### 3. Accesos

- Menú de prueba: `http://localhost:8080/menu`
- API: `http://localhost:8080/api`

## API Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/mesas` | Crear mesa |
| `GET` | `/api/mesas` | Listar todas las mesas |
| `GET` | `/api/mesas/{id}` | Obtener mesa por ID |
| `GET` | `/api/mesas/{id}/qr` | Obtener código QR de la mesa |
| `POST` | `/api/pedidos/mesa/{mesaId}` | Crear pedido para mesa |
| `POST` | `/api/pedidos/mesa/{mesaId}/items` | Agregar ítem al pedido |
| `GET` | `/api/pedidos/mesa/{mesaId}` | Ver resumen del pedido actual |
| `POST` | `/api/pagos` | Procesar pago |

## WebSocket

- Endpoint: `/ws`
- Tópicos: `/topic/mesas`

## Estructura del proyecto

```
src/main/java/com/restaurant/
├── AplicacionMesaQR.java          # Clase principal
├── modelo/                         # Entidades JPA
├── repositorio/                    # Repositorios Spring Data
├── servicio/                       # Lógica de negocio
├── controlador/                    # Controladores REST
├── dto/                            # DTOs y eventos WebSocket
├── configuracion/                  # Configuración (WebSocket)
└── excepcion/                      # Manejo de excepciones
```
