CREATE TABLE pagos (
    id                   BIGSERIAL PRIMARY KEY,
    pedido_id            BIGINT        NOT NULL REFERENCES pedidos(id),
    metodo               VARCHAR(20)   NOT NULL,
    estado               VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    monto                DECIMAL(12,2) NOT NULL,
    referencia_proveedor VARCHAR(500),
    creado_en            TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pagos_pedido ON pagos(pedido_id);
