CREATE TABLE pedidos (
    id          BIGSERIAL PRIMARY KEY,
    mesa_id     BIGINT        NOT NULL REFERENCES mesas(id),
    estado      VARCHAR(20)   NOT NULL DEFAULT 'ABIERTO',
    creado_en   TIMESTAMP     NOT NULL DEFAULT NOW(),
    pagado_en   TIMESTAMP,
    version     BIGINT        NOT NULL DEFAULT 0
);

CREATE INDEX idx_pedidos_mesa_estado ON pedidos(mesa_id, estado);
