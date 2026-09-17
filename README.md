# Sistema de Alquiler de Computadoras

Sistema de escritorio desarrollado con Java Swing, JDBC, MySQL y Maven para gestionar el alquiler de computadoras, calcular el importe de uso y consultar el historial de sesiones.

## Modelo lógico

### CLIENTE
- `IdCliente` PK
- `NombreCliente`
- `NombreUsuario`

### TARIFA
- `IdTarifa` PK
- `TipoComputadora`
- `CostoHora`

### COMPUTADORA
- `IdComputadora` PK
- `IdTarifa` FK
- `Estado`
- `NumeroEquipo`

### SESION_ALQUILER
- `IdSesion` PK
- `IdCliente` FK
- `IdComputadora` FK
- `HoraInicio`
- `HoraFin`
- `TiempoUsado`
- `MontoCalculado`

## Diagrama de clases

```text
Cliente 1 ─────── N SesionAlquiler N ─────── 1 Computadora
                                              N
                                              │
                                              1
                                            Tarifa