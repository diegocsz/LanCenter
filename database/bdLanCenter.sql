
CREATE DATABASE IF NOT EXISTS lan_center;
USE lan_center;

-- 1. CLIENTES
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    dni VARCHAR(15) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    correo VARCHAR(100),
    fecha_registro DATE NOT NULL DEFAULT (CURRENT_DATE)
);

-- 2. EMPLEADOS
CREATE TABLE IF NOT EXISTS empleados (
    id_empleado INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    dni VARCHAR(15) NOT NULL UNIQUE,
    cargo VARCHAR(50) NOT NULL,
    turno VARCHAR(20) NOT NULL,
    fecha_contratacion DATE NOT NULL DEFAULT (CURRENT_DATE)
);

-- 3. USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado INT NOT NULL UNIQUE,
    correo VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'EMPLEADO',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado),
    CHECK (rol IN ('ADMIN', 'EMPLEADO'))
);

-- 4. TARIFAS
CREATE TABLE IF NOT EXISTS tarifa (
    id_tarifa INT AUTO_INCREMENT PRIMARY KEY,
    tipo_equipo VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(100),
    precio_hora DECIMAL(10,2) NOT NULL CHECK (precio_hora > 0),
    CHECK (tipo_equipo IN ('PC Gamer', 'PC Normal', 'Consola'))
);

-- 5. COMPUTADORAS GAMER
CREATE TABLE IF NOT EXISTS computadoras_gamer (
    id_pc_gamer INT AUTO_INCREMENT PRIMARY KEY,
    nombre_pc VARCHAR(30) NOT NULL UNIQUE,
    especificaciones VARCHAR(200),
    estado VARCHAR(20) NOT NULL DEFAULT 'Disponible',
    id_tarifa INT NOT NULL,
    FOREIGN KEY (id_tarifa) REFERENCES tarifa(id_tarifa),
    CHECK (estado IN ('Disponible', 'Ocupada', 'Mantenimiento'))
);

-- 6. COMPUTADORAS NORMALES
CREATE TABLE IF NOT EXISTS computadoras_normales (
    id_pc_normal INT AUTO_INCREMENT PRIMARY KEY,
    nombre_pc VARCHAR(30) NOT NULL UNIQUE,
    especificaciones VARCHAR(200),
    estado VARCHAR(20) NOT NULL DEFAULT 'Disponible',
    id_tarifa INT NOT NULL,
    FOREIGN KEY (id_tarifa) REFERENCES tarifa(id_tarifa),
    CHECK (estado IN ('Disponible', 'Ocupada', 'Mantenimiento'))
);

-- 7. CONSOLAS
CREATE TABLE IF NOT EXISTS consolas (
    id_consola INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    tipo_consola VARCHAR(30) NOT NULL,
    modelo VARCHAR(50),
    estado VARCHAR(20) NOT NULL DEFAULT 'Disponible',
    id_tarifa INT NOT NULL,
    FOREIGN KEY (id_tarifa) REFERENCES tarifa(id_tarifa),
    CHECK (estado IN ('Disponible', 'Ocupada', 'Mantenimiento'))
);

-- 8. SESIONES DE ALQUILER
CREATE TABLE IF NOT EXISTS sesion_alquiler (
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

    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado),
    FOREIGN KEY (id_pc_gamer) REFERENCES computadoras_gamer(id_pc_gamer),
    FOREIGN KEY (id_pc_normal) REFERENCES computadoras_normales(id_pc_normal),
    FOREIGN KEY (id_consola) REFERENCES consolas(id_consola),

    CHECK ((id_pc_gamer IS NOT NULL) +
           (id_pc_normal IS NOT NULL) +
           (id_consola IS NOT NULL) = 1),
    CHECK (hora_fin_programada > hora_inicio),
    CHECK (hora_fin_real IS NULL OR hora_fin_real >= hora_inicio),
    CHECK (precio_hora_aplicado > 0),
    CHECK (total_pagado IS NULL OR total_pagado >= 0),
    CHECK (estado IN ('ACTIVA', 'FINALIZADA', 'CANCELADA'))
);

-- 9. TICKETS
CREATE TABLE IF NOT EXISTS ticket (
    id_ticket INT AUTO_INCREMENT PRIMARY KEY,
    id_sesion INT NOT NULL UNIQUE,
    fecha_ticket DATE NOT NULL DEFAULT (CURRENT_DATE),
    hora_emision TIME NOT NULL DEFAULT (CURRENT_TIME),
    monto_alquiler DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_sesion) REFERENCES sesion_alquiler(id_sesion),
    CHECK (monto_alquiler >= 0)
);


-- INSERCION DE DATOS 


USE lan_center;

-- Tarifas de prueba
INSERT IGNORE INTO tarifa (tipo_equipo, descripcion, precio_hora)
VALUES
('PC Gamer', 'Computadora Gamer', 4.00),
('PC Normal', 'Computadora Normal', 3.00),
('Consola', 'Consola de videojuegos', 5.00);

SET @gamer = (SELECT id_tarifa FROM tarifa WHERE tipo_equipo = 'PC Gamer');
SET @normal = (SELECT id_tarifa FROM tarifa WHERE tipo_equipo = 'PC Normal');
SET @consola = (SELECT id_tarifa FROM tarifa WHERE tipo_equipo = 'Consola');

-- 8 computadoras Gamer
INSERT IGNORE INTO computadoras_gamer (nombre_pc, id_tarifa)
VALUES
('PC01-GAMER', @gamer),
('PC02-GAMER', @gamer),
('PC03-GAMER', @gamer),
('PC04-GAMER', @gamer),
('PC05-GAMER', @gamer),
('PC06-GAMER', @gamer),
('PC07-GAMER', @gamer),
('PC08-GAMER', @gamer);

-- 4 computadoras normales
INSERT IGNORE INTO computadoras_normales (nombre_pc, id_tarifa)
VALUES
('PC09-NORMAL', @normal),
('PC10-NORMAL', @normal),
('PC11-NORMAL', @normal),
('PC12-NORMAL', @normal);

-- 5 consolas
INSERT IGNORE INTO consolas (codigo, tipo_consola, id_tarifa)
VALUES
('PS1', 'PlayStation', @consola),
('PS2', 'PlayStation', @consola),
('XBOX1', 'Xbox', @consola),
('SWITCH1', 'Nintendo Switch', @consola),
('SWITCH2', 'Nintendo Switch', @consola);

-- MOSTRAR TABLAS

USE lan_center;

SHOW TABLES;

SELECT COUNT(*) AS total_gamer FROM computadoras_gamer;
SELECT COUNT(*) AS total_normales FROM computadoras_normales;
SELECT COUNT(*) AS total_consolas FROM consolas;


USE lan_center;

SELECT * FROM cliente;
