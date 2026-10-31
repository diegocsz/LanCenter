DROP DATABASE IF EXISTS lan_center;

CREATE DATABASE lan_center
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE lan_center;

CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    dni VARCHAR(15) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    correo VARCHAR(100),
    fecha_registro DATE NOT NULL DEFAULT (CURRENT_DATE)
);

CREATE TABLE empleados (
    id_empleado INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    dni VARCHAR(15) NOT NULL UNIQUE,
    cargo VARCHAR(50) NOT NULL,
    turno VARCHAR(20) NOT NULL,
    fecha_contratacion DATE NOT NULL DEFAULT (CURRENT_DATE)
);

CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado INT NOT NULL UNIQUE,
    correo VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'EMPLEADO',
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (id_empleado)
        REFERENCES empleados(id_empleado),

    CHECK (rol IN ('ADMIN', 'EMPLEADO'))
);

CREATE TABLE tarifa (
    id_tarifa INT AUTO_INCREMENT PRIMARY KEY,
    tipo_equipo VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(100),
    precio_hora DECIMAL(10,2) NOT NULL,

    CHECK (precio_hora > 0),

    CHECK (
        tipo_equipo IN ('PC Gamer', 'PC Normal', 'Consola')
    )
);

CREATE TABLE computadoras_gamer (
    id_pc_gamer INT AUTO_INCREMENT PRIMARY KEY,
    nombre_pc VARCHAR(30) NOT NULL UNIQUE,
    especificaciones VARCHAR(200),
    estado VARCHAR(20) NOT NULL DEFAULT 'Disponible',
    id_tarifa INT NOT NULL,

    FOREIGN KEY (id_tarifa)
        REFERENCES tarifa(id_tarifa),

    CHECK (
        estado IN ('Disponible', 'Ocupada', 'Mantenimiento')
    )
);

CREATE TABLE computadoras_normales (
    id_pc_normal INT AUTO_INCREMENT PRIMARY KEY,
    nombre_pc VARCHAR(30) NOT NULL UNIQUE,
    especificaciones VARCHAR(200),
    estado VARCHAR(20) NOT NULL DEFAULT 'Disponible',
    id_tarifa INT NOT NULL,

    FOREIGN KEY (id_tarifa)
        REFERENCES tarifa(id_tarifa),

    CHECK (
        estado IN ('Disponible', 'Ocupada', 'Mantenimiento')
    )
);

CREATE TABLE consolas (
    id_consola INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    tipo_consola VARCHAR(30) NOT NULL,
    modelo VARCHAR(50),
    estado VARCHAR(20) NOT NULL DEFAULT 'Disponible',
    id_tarifa INT NOT NULL,

    FOREIGN KEY (id_tarifa)
        REFERENCES tarifa(id_tarifa),

    CHECK (
        estado IN ('Disponible', 'Ocupada', 'Mantenimiento')
    )
);

CREATE TABLE sesion_alquiler (
    id_sesion INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_empleado INT NOT NULL,

    id_pc_gamer INT NULL,
    id_pc_normal INT NULL,
    id_consola INT NULL,

    hora_inicio DATETIME NOT NULL,
    hora_fin_programada DATETIME NOT NULL,
    hora_fin_real DATETIME NULL,

    precio_hora_aplicado DECIMAL(10,2) NOT NULL,
    total_pagado DECIMAL(10,2) NULL,

    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',

    FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente),

    FOREIGN KEY (id_empleado)
        REFERENCES empleados(id_empleado),

    FOREIGN KEY (id_pc_gamer)
        REFERENCES computadoras_gamer(id_pc_gamer),

    FOREIGN KEY (id_pc_normal)
        REFERENCES computadoras_normales(id_pc_normal),

    FOREIGN KEY (id_consola)
        REFERENCES consolas(id_consola),

    CHECK (
        (id_pc_gamer IS NOT NULL) +
        (id_pc_normal IS NOT NULL) +
        (id_consola IS NOT NULL) = 1
    ),

    CHECK (hora_fin_programada > hora_inicio),

    CHECK (
        hora_fin_real IS NULL
        OR hora_fin_real >= hora_inicio
    ),

    CHECK (precio_hora_aplicado > 0),

    CHECK (
        total_pagado IS NULL
        OR total_pagado >= 0
    ),

    CHECK (
        estado IN ('ACTIVA', 'FINALIZADA', 'CANCELADA')
    )
);

CREATE TABLE ticket (
    id_ticket INT AUTO_INCREMENT PRIMARY KEY,
    id_sesion INT NOT NULL UNIQUE,

    fecha_ticket DATE NOT NULL DEFAULT (CURRENT_DATE),
    hora_emision TIME NOT NULL DEFAULT (CURRENT_TIME),

    monto_alquiler DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (id_sesion)
        REFERENCES sesion_alquiler(id_sesion),

    CHECK (monto_alquiler >= 0)
);

INSERT INTO tarifa (tipo_equipo, descripcion, precio_hora)
VALUES
('PC Gamer', 'Computadora Gamer', 4.00),
('PC Normal', 'Computadora Normal', 3.00),
('Consola', 'Consola de videojuegos', 5.00);

SET @gamer = (
    SELECT id_tarifa
    FROM tarifa
    WHERE tipo_equipo = 'PC Gamer'
);

SET @normal = (
    SELECT id_tarifa
    FROM tarifa
    WHERE tipo_equipo = 'PC Normal'
);

SET @consola = (
    SELECT id_tarifa
    FROM tarifa
    WHERE tipo_equipo = 'Consola'
);

INSERT INTO computadoras_gamer
(nombre_pc, especificaciones, estado, id_tarifa)
VALUES
('PC01-GAMER', 'Intel Core i5, 16 GB RAM, RTX 3060', 'Ocupada', @gamer),
('PC02-GAMER', 'Intel Core i5, 16 GB RAM, RTX 3060', 'Ocupada', @gamer),
('PC03-GAMER', 'Intel Core i5, 16 GB RAM, RTX 3060', 'Ocupada', @gamer),
('PC04-GAMER', 'Intel Core i5, 16 GB RAM, RTX 3060', 'Ocupada', @gamer),
('PC05-GAMER', 'Intel Core i5, 16 GB RAM, RTX 3060', 'Ocupada', @gamer),
('PC06-GAMER', 'Intel Core i5, 16 GB RAM, RTX 3060', 'Ocupada', @gamer),
('PC07-GAMER', 'Intel Core i7, 32 GB RAM, RTX 4070', 'Disponible', @gamer),
('PC08-GAMER', 'Intel Core i7, 32 GB RAM, RTX 4070', 'Disponible', @gamer);

INSERT INTO computadoras_normales
(nombre_pc, especificaciones, estado, id_tarifa)
VALUES
('PC09-NORMAL', 'Intel Core i3, 8 GB RAM', 'Disponible', @normal),
('PC10-NORMAL', 'Intel Core i3, 8 GB RAM', 'Disponible', @normal),
('PC11-NORMAL', 'Intel Core i3, 8 GB RAM', 'Disponible', @normal),
('PC12-NORMAL', 'Intel Core i3, 8 GB RAM', 'Disponible', @normal);

INSERT INTO consolas
(codigo, tipo_consola, modelo, estado, id_tarifa)
VALUES
('PS1', 'PlayStation', 'PlayStation 5', 'Ocupada', @consola),
('PS2', 'PlayStation', 'PlayStation 5', 'Ocupada', @consola),
('XBOX1', 'Xbox', 'Xbox Series X', 'Disponible', @consola),
('SWITCH1', 'Nintendo Switch', 'Nintendo Switch OLED', 'Disponible', @consola),
('SWITCH2', 'Nintendo Switch', 'Nintendo Switch OLED', 'Disponible', @consola);

INSERT INTO empleados
(nombre, apellido, dni, cargo, turno)
VALUES
('Diego', 'Salcedo', '60888831', 'Administrador', 'Mañana'),
('Carlos', 'Ramirez', '60888832', 'Empleado', 'Tarde'),
('Maria', 'Torres', '60888833', 'Empleado', 'Noche');

INSERT INTO usuarios
(id_empleado, correo, password_hash, rol, activo)
VALUES
(
    (SELECT id_empleado FROM empleados WHERE dni = '60888831'),
    'admin@lancenter.com',
    '123456',
    'ADMIN',
    TRUE
),
(
    (SELECT id_empleado FROM empleados WHERE dni = '60888832'),
    'carlos@lancenter.com',
    '123456',
    'EMPLEADO',
    TRUE
),
(
    (SELECT id_empleado FROM empleados WHERE dni = '60888833'),
    'maria@lancenter.com',
    '123456',
    'EMPLEADO',
    TRUE
);

INSERT INTO cliente
(nombre, apellido, dni, telefono, correo)
VALUES
('Juan', 'Perez', '70000001', '987654321', 'juan.perez@gmail.com'),
('Luis', 'Gonzales', '70000002', '987654322', 'luis.gonzales@gmail.com'),
('Ana', 'Martinez', '70000003', '987654323', 'ana.martinez@gmail.com'),
('Pedro', 'Ramirez', '70000004', '987654324', 'pedro.ramirez@gmail.com'),
('Sofia', 'Torres', '70000005', '987654325', 'sofia.torres@gmail.com'),
('Miguel', 'Flores', '70000006', '987654326', 'miguel.flores@gmail.com');

DELIMITER $$

CREATE TRIGGER trg_calcular_total_sesion
BEFORE UPDATE ON sesion_alquiler
FOR EACH ROW
BEGIN

    IF NEW.estado = 'FINALIZADA'
       AND OLD.estado <> 'FINALIZADA' THEN

        SET NEW.hora_fin_real = COALESCE(
            NEW.hora_fin_real,
            NOW()
        );

        SET NEW.total_pagado = ROUND(
            TIMESTAMPDIFF(
                SECOND,
                NEW.hora_inicio,
                NEW.hora_fin_real
            ) / 3600.0 * NEW.precio_hora_aplicado,
            2
        );

    END IF;

END$$

CREATE TRIGGER trg_generar_ticket
AFTER INSERT ON sesion_alquiler
FOR EACH ROW
BEGIN

    INSERT INTO ticket (
        id_sesion,
        fecha_ticket,
        hora_emision,
        monto_alquiler
    )
    VALUES (
        NEW.id_sesion,
        CURRENT_DATE,
        CURRENT_TIME,

        ROUND(
            TIMESTAMPDIFF(
                SECOND,
                NEW.hora_inicio,
                NEW.hora_fin_programada
            ) / 3600.0 * NEW.precio_hora_aplicado,
            2
        )
    );

END$$

DELIMITER ;

INSERT INTO sesion_alquiler
(
    id_cliente,
    id_empleado,
    id_pc_gamer,
    hora_inicio,
    hora_fin_programada,
    precio_hora_aplicado,
    estado
)
VALUES
(
    (SELECT id_cliente FROM cliente WHERE dni = '70000001'),
    (SELECT id_empleado FROM empleados WHERE dni = '60888831'),
    (SELECT id_pc_gamer FROM computadoras_gamer WHERE nombre_pc = 'PC01-GAMER'),
    DATE_SUB(NOW(), INTERVAL 30 MINUTE),
    DATE_ADD(NOW(), INTERVAL 30 MINUTE),
    4.00,
    'ACTIVA'
);


INSERT INTO sesion_alquiler
(
    id_cliente,
    id_empleado,
    id_pc_gamer,
    hora_inicio,
    hora_fin_programada,
    precio_hora_aplicado,
    estado
)
VALUES
(
    (SELECT id_cliente FROM cliente WHERE dni = '70000002'),
    (SELECT id_empleado FROM empleados WHERE dni = '60888832'),
    (SELECT id_pc_gamer FROM computadoras_gamer WHERE nombre_pc = 'PC02-GAMER'),
    DATE_SUB(NOW(), INTERVAL 2 HOUR),
    DATE_SUB(NOW(), INTERVAL 1 HOUR),
    4.00,
    'ACTIVA'
);


INSERT INTO sesion_alquiler
(
    id_cliente,
    id_empleado,
    id_consola,
    hora_inicio,
    hora_fin_programada,
    precio_hora_aplicado,
    estado
)
VALUES
(
    (SELECT id_cliente FROM cliente WHERE dni = '70000003'),
    (SELECT id_empleado FROM empleados WHERE dni = '60888832'),
    (SELECT id_consola FROM consolas WHERE codigo = 'PS1'),
    DATE_SUB(NOW(), INTERVAL 3 HOUR),
    DATE_SUB(NOW(), INTERVAL 1 HOUR),
    5.00,
    'ACTIVA'
);

SHOW TABLES;

SELECT COUNT(*) AS total_clientes FROM cliente;
SELECT COUNT(*) AS total_empleados FROM empleados;
SELECT COUNT(*) AS total_usuarios FROM usuarios;
SELECT COUNT(*) AS total_gamer FROM computadoras_gamer;
SELECT COUNT(*) AS total_normales FROM computadoras_normales;
SELECT COUNT(*) AS total_consolas FROM consolas;
SELECT COUNT(*) AS total_sesiones FROM sesion_alquiler;
SELECT COUNT(*) AS total_tickets FROM ticket;

SELECT
    u.id_usuario,
    e.nombre,
    e.apellido,
    u.correo,
    u.rol,
    u.activo
FROM usuarios u
INNER JOIN empleados e
    ON u.id_empleado = e.id_empleado;

SELECT * FROM cliente;

SELECT * FROM computadoras_gamer;
SELECT * FROM computadoras_normales;
SELECT * FROM consolas;

SELECT
    s.id_sesion,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    e.nombre AS empleado,
    s.hora_inicio,
    s.hora_fin_programada,
    s.estado,
    s.precio_hora_aplicado,
    t.id_ticket,
    t.monto_alquiler
FROM sesion_alquiler s
INNER JOIN cliente c
    ON s.id_cliente = c.id_cliente
INNER JOIN empleados e
    ON s.id_empleado = e.id_empleado
LEFT JOIN ticket t
    ON s.id_sesion = t.id_sesion;