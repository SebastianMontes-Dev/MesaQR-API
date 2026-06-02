CREATE TABLE mesas (
    id            BIGSERIAL PRIMARY KEY,
    numero_mesa   INTEGER      NOT NULL UNIQUE,
    capacidad     INTEGER      NOT NULL DEFAULT 4,
    estado        VARCHAR(20)  NOT NULL DEFAULT 'DISPONIBLE',
    codigo_qr     VARCHAR(500)
);

CREATE TABLE platillos (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(200)   NOT NULL,
    descripcion   VARCHAR(1000),
    precio        DECIMAL(12,2)  NOT NULL,
    categoria     VARCHAR(100),
    disponible    BOOLEAN        NOT NULL DEFAULT TRUE,
    url_imagen    VARCHAR(500)
);

INSERT INTO platillos (nombre, descripcion, precio, categoria) VALUES
    ('Hamburguesa clásica', 'Carne 150g, queso, lechuga, tomate', 14000, 'PLATOS'),
    ('Pizza margarita', 'Mozzarella, albahaca, salsa de tomate', 18000, 'PLATOS'),
    ('Agua mineral', 'Botella 500ml', 4000, 'BEBIDAS'),
    ('Cerveza artesanal', 'IPA local 330ml', 9000, 'BEBIDAS'),
    ('Café', 'Americano', 3500, 'BEBIDAS'),
    ('Tiramisú', 'Porción individual', 10000, 'POSTRES');
