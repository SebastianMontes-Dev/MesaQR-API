CREATE TABLE detalles_pedido (
    id           BIGSERIAL PRIMARY KEY,
    pedido_id    BIGINT        NOT NULL REFERENCES pedidos(id),
    platillo_id  BIGINT        NOT NULL REFERENCES platillos(id),
    cantidad     INTEGER       NOT NULL DEFAULT 1,
    precio       DECIMAL(12,2) NOT NULL,
    notas        VARCHAR(500)
);

CREATE INDEX idx_detalles_pedido ON detalles_pedido(pedido_id);
