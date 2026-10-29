package dao;

import config.ConexionMySQL;

import modelo.Equipo;

import java.math.BigDecimal;

import java.math.RoundingMode;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Timestamp;

import java.time.Duration;

import java.time.LocalDate;

import java.time.LocalDateTime;

import java.util.ArrayList;

import java.util.List;

public class SesionDAO {

    public record Cliente(int id, String nombre, String dni) {

        @Override

        public String toString() {

            return nombre + " (" + dni + ")";

        }

    }

    public record TicketInfo(
            int id,
            String cliente,
            String equipo,
            LocalDateTime inicio,
            LocalDateTime fin,
            BigDecimal monto) {

    }

    public record TicketHistorial(
            int idTicket,
            int idSesion,
            String cliente,
            String dni,
            String equipo,
            String tipo,
            String empleado,
            LocalDateTime inicio,
            LocalDateTime fin,
            BigDecimal monto) {

    }

    public record Visita(
            int id,
            String cliente,
            String dni,
            String empleado,
            String tipo,
            String equipo,
            LocalDateTime inicio,
            LocalDateTime finProgramada,
            LocalDateTime finReal,
            BigDecimal precioHora,
            BigDecimal monto,
            String estado,
            Integer ticket) {

    }

    private record TablaEquipo(String tabla, String id) {

    }

    private TablaEquipo tabla(String tipo) {

        return switch (tipo) {

            case "PC Gamer" ->

                new TablaEquipo("computadoras_gamer", "id_pc_gamer");

            case "PC Normal" ->

                new TablaEquipo("computadoras_normales", "id_pc_normal");

            case "Consola" ->

                new TablaEquipo("consolas", "id_consola");

            default ->

                throw new IllegalArgumentException("Tipo de equipo desconocido");

        };

    }

    public List<Cliente> listarClientes() throws SQLException {

        List<Cliente> clientes = new ArrayList<>();

        String sql = """

                SELECT id_cliente, nombre, apellido, dni

                FROM cliente

                ORDER BY nombre, apellido

                """;

        try (Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                clientes.add(new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre") + " " + rs.getString("apellido"),
                        rs.getString("dni")
                ));

            }

        }

        return clientes;

    }

    public List<Cliente> buscarClientes(String criterio) throws SQLException {

        if (criterio == null || criterio.isBlank()) {

            return List.of();

        }

        List<Cliente> clientes = new ArrayList<>();

        String sql = """

                SELECT id_cliente, nombre, apellido, dni

                FROM cliente

                WHERE LOCATE(LOWER(?), LOWER(CONCAT(nombre, ' ', apellido))) > 0

                   OR LOCATE(?, dni) > 0

                   OR LOCATE(LOWER(?), LOWER(COALESCE(correo, ''))) > 0

                ORDER BY nombre, apellido

                LIMIT 8

                """;

        String texto = criterio.trim();

        try (Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, texto);

            ps.setString(2, texto);

            ps.setString(3, texto);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    clientes.add(new Cliente(
                            rs.getInt("id_cliente"),
                            rs.getString("nombre") + " " + rs.getString("apellido"),
                            rs.getString("dni")
                    ));

                }

            }

        }

        return clientes;

    }

    public void iniciar(
            int idCliente,
            int idEmpleado,
            Equipo equipo,
            LocalDateTime finProgramada) throws SQLException {

        TablaEquipo tablaEquipo = tabla(equipo.getTipo());

        try (Connection cn = ConexionMySQL.conectar()) {

            cn.setAutoCommit(false);

            try {

                int idTarifa;

                String estadoEquipo;

                String sqlEquipo = "SELECT estado, id_tarifa FROM "
                        + tablaEquipo.tabla()
                        + " WHERE " + tablaEquipo.id() + " = ? FOR UPDATE";

                try (PreparedStatement ps = cn.prepareStatement(sqlEquipo)) {

                    ps.setInt(1, equipo.getId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            throw new SQLException("Equipo no encontrado");

                        }

                        estadoEquipo = rs.getString("estado");

                        idTarifa = rs.getInt("id_tarifa");

                    }

                }

                if (!"Disponible".equals(estadoEquipo)) {

                    throw new SQLException("El equipo ya no está disponible");

                }

                BigDecimal precioHora;

                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT precio_hora FROM tarifa WHERE id_tarifa = ?")) {

                    ps.setInt(1, idTarifa);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            throw new SQLException("Tarifa no encontrada");

                        }

                        precioHora = rs.getBigDecimal("precio_hora");

                    }

                }

                LocalDateTime inicio = LocalDateTime.now();

                if (finProgramada == null || !finProgramada.isAfter(inicio)) {

                    throw new SQLException(
                            "La hora de fin debe ser posterior al inicio"
                    );

                }

                String sqlSesion = """

                        INSERT INTO sesion_alquiler (

                            id_cliente,

                            id_empleado,

                            %s,

                            hora_inicio,

                            hora_fin_programada,

                            precio_hora_aplicado,

                            estado

                        ) VALUES (?, ?, ?, ?, ?, ?, 'ACTIVA')

                        """.formatted(tablaEquipo.id());

                try (PreparedStatement ps = cn.prepareStatement(sqlSesion)) {

                    ps.setInt(1, idCliente);

                    ps.setInt(2, idEmpleado);

                    ps.setInt(3, equipo.getId());

                    ps.setTimestamp(4, Timestamp.valueOf(inicio));

                    ps.setTimestamp(5, Timestamp.valueOf(finProgramada));

                    ps.setBigDecimal(6, precioHora);

                    ps.executeUpdate();

                }

                String sqlActualizarEquipo = "UPDATE "
                        + tablaEquipo.tabla()
                        + " SET estado = 'Ocupada' WHERE "
                        + tablaEquipo.id() + " = ?";

                try (PreparedStatement ps = cn.prepareStatement(sqlActualizarEquipo)) {

                    ps.setInt(1, equipo.getId());

                    ps.executeUpdate();

                }

                cn.commit();

            } catch (Exception e) {

                cn.rollback();

                if (e instanceof SQLException sql) {

                    throw sql;

                }

                throw new SQLException("No se pudo iniciar la sesión", e);

            }

        }

    }

    public void terminar(
            String rol,
            int idSesion,
            boolean cancelar) throws SQLException {

        if (!"ADMIN".equals(rol)) {

            throw new SecurityException(
                    "Acceso solo para administradores"
            );

        }

        try (Connection cn = ConexionMySQL.conectar()) {

            cn.setAutoCommit(false);

            try {

                int idEquipo;

                String tipo;

                LocalDateTime inicio;

                LocalDateTime finProgramada;

                BigDecimal precioHora;

                String sqlSesion = """

                        SELECT id_pc_gamer, id_pc_normal, id_consola,

                               hora_inicio, hora_fin_programada,

                               precio_hora_aplicado, estado

                        FROM sesion_alquiler

                        WHERE id_sesion = ?

                        FOR UPDATE

                        """;

                try (PreparedStatement ps = cn.prepareStatement(sqlSesion)) {

                    ps.setInt(1, idSesion);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            throw new SQLException("Sesión no encontrada");

                        }

                        if (!"ACTIVA".equals(rs.getString("estado"))) {

                            throw new SQLException("Esta sesión ya terminó");

                        }

                        if (rs.getObject("id_pc_gamer") != null) {

                            tipo = "PC Gamer";

                            idEquipo = rs.getInt("id_pc_gamer");

                        } else if (rs.getObject("id_pc_normal") != null) {

                            tipo = "PC Normal";

                            idEquipo = rs.getInt("id_pc_normal");

                        } else {

                            tipo = "Consola";

                            idEquipo = rs.getInt("id_consola");

                        }

                        inicio = rs.getTimestamp("hora_inicio").toLocalDateTime();

                        finProgramada = rs.getTimestamp(
                                "hora_fin_programada"
                        ).toLocalDateTime();

                        precioHora = rs.getBigDecimal("precio_hora_aplicado");

                    }

                }

                TablaEquipo tablaEquipo = tabla(tipo);

                String sqlBloquearEquipo = "SELECT "
                        + tablaEquipo.id()
                        + " FROM " + tablaEquipo.tabla()
                        + " WHERE " + tablaEquipo.id() + " = ? FOR UPDATE";

                try (PreparedStatement ps = cn.prepareStatement(sqlBloquearEquipo)) {

                    ps.setInt(1, idEquipo);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            throw new SQLException("Equipo no encontrado");

                        }

                    }

                }

                LocalDateTime fin = LocalDateTime.now();

                LocalDateTime finCobro = cancelar ? finProgramada : fin;

                long segundos = Math.max(
                        0,
                        Duration.between(inicio, finCobro).getSeconds()
                );

                BigDecimal monto = precioHora
                        .multiply(BigDecimal.valueOf(segundos))
                        .divide(
                                BigDecimal.valueOf(3600),
                                2,
                                RoundingMode.HALF_UP
                        );

                String sqlActualizarSesion = """

                        UPDATE sesion_alquiler

                        SET hora_fin_real = ?, total_pagado = ?, estado = ?

                        WHERE id_sesion = ?

                        """;

                try (PreparedStatement ps = cn.prepareStatement(sqlActualizarSesion)) {

                    ps.setTimestamp(1, Timestamp.valueOf(fin));

                    ps.setBigDecimal(2, monto);

                    ps.setString(3, cancelar ? "CANCELADA" : "FINALIZADA");

                    ps.setInt(4, idSesion);

                    ps.executeUpdate();

                }

                String sqlTicket = """

                        INSERT INTO ticket (id_sesion, monto_alquiler)

                        VALUES (?, ?)

                        """;

                try (PreparedStatement ps = cn.prepareStatement(sqlTicket)) {

                    ps.setInt(1, idSesion);

                    ps.setBigDecimal(2, monto);

                    ps.executeUpdate();

                }

                String sqlLiberarEquipo = "UPDATE "
                        + tablaEquipo.tabla()
                        + " SET estado = 'Disponible' WHERE "
                        + tablaEquipo.id() + " = ?";

                try (PreparedStatement ps = cn.prepareStatement(sqlLiberarEquipo)) {

                    ps.setInt(1, idEquipo);

                    ps.executeUpdate();

                }

                cn.commit();

            } catch (Exception e) {

                cn.rollback();

                if (e instanceof SQLException sql) {

                    throw sql;

                }

                throw new SQLException("No se pudo terminar la sesión", e);

            }

        }

    }

    public List<TicketInfo> listarTickets() throws SQLException {

        List<TicketInfo> tickets = new ArrayList<>();

        String sql = """

                SELECT t.id_ticket,

                       CONCAT(c.nombre, ' ', c.apellido) AS cliente,

                       COALESCE(g.nombre_pc, n.nombre_pc, k.codigo) AS equipo,

                       s.hora_inicio,

                       COALESCE(s.hora_fin_real, s.hora_fin_programada) AS hora_fin,

                       t.monto_alquiler

                FROM ticket t

                INNER JOIN sesion_alquiler s ON s.id_sesion = t.id_sesion

                INNER JOIN cliente c ON c.id_cliente = s.id_cliente

                LEFT JOIN computadoras_gamer g ON g.id_pc_gamer = s.id_pc_gamer

                LEFT JOIN computadoras_normales n ON n.id_pc_normal = s.id_pc_normal

                LEFT JOIN consolas k ON k.id_consola = s.id_consola

                WHERE t.fecha_ticket = CURDATE()

                ORDER BY t.hora_emision DESC, t.id_ticket DESC

                """;

        try (Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Timestamp inicio = rs.getTimestamp("hora_inicio");

                Timestamp fin = rs.getTimestamp("hora_fin");

                tickets.add(new TicketInfo(
                        rs.getInt("id_ticket"),
                        rs.getString("cliente"),
                        rs.getString("equipo"),
                        inicio == null ? null : inicio.toLocalDateTime(),
                        fin == null ? null : fin.toLocalDateTime(),
                        rs.getBigDecimal("monto_alquiler")
                ));

            }

        }

        return tickets;

    }

    public int contarTicketsHistoricos() throws SQLException {

        String sql = "SELECT COUNT(*) FROM ticket";

        try (Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        }

    }

    public List<TicketHistorial> listarHistorialTickets(
            Integer idCliente) throws SQLException {

        List<TicketHistorial> tickets = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""

                SELECT t.id_ticket,

                       s.id_sesion,

                       CONCAT(c.nombre, ' ', c.apellido) AS cliente,

                       c.dni,

                       COALESCE(g.nombre_pc, n.nombre_pc, k.codigo) AS equipo,

                       CASE

                           WHEN s.id_pc_gamer IS NOT NULL THEN 'PC Gamer'

                           WHEN s.id_pc_normal IS NOT NULL THEN 'PC Normal'

                           ELSE 'Consola'

                       END AS tipo,

                       CONCAT(e.nombre, ' ', e.apellido) AS empleado,

                       s.hora_inicio,

                       COALESCE(s.hora_fin_real, s.hora_fin_programada) AS hora_fin,

                       t.monto_alquiler

                FROM ticket t

                INNER JOIN sesion_alquiler s ON s.id_sesion = t.id_sesion

                INNER JOIN cliente c ON c.id_cliente = s.id_cliente

                INNER JOIN empleados e ON e.id_empleado = s.id_empleado

                LEFT JOIN computadoras_gamer g ON g.id_pc_gamer = s.id_pc_gamer

                LEFT JOIN computadoras_normales n ON n.id_pc_normal = s.id_pc_normal

                LEFT JOIN consolas k ON k.id_consola = s.id_consola

                """);

        if (idCliente != null) {

            sql.append(" WHERE s.id_cliente = ?");

        }

        sql.append(" ORDER BY t.id_ticket DESC");

        try (Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            if (idCliente != null) {

                ps.setInt(1, idCliente);

            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Timestamp inicio = rs.getTimestamp("hora_inicio");

                    Timestamp fin = rs.getTimestamp("hora_fin");

                    tickets.add(new TicketHistorial(
                            rs.getInt("id_ticket"),
                            rs.getInt("id_sesion"),
                            rs.getString("cliente"),
                            rs.getString("dni"),
                            rs.getString("equipo"),
                            rs.getString("tipo"),
                            rs.getString("empleado"),
                            inicio == null ? null : inicio.toLocalDateTime(),
                            fin == null ? null : fin.toLocalDateTime(),
                            rs.getBigDecimal("monto_alquiler")
                    ));

                }

            }

        }

        return tickets;

    }

    public List<Visita> listar(
            String rol,
            int idCliente,
            LocalDate desde,
            LocalDate hasta,
            String tipo,
            String estado) throws SQLException {

        if (!"ADMIN".equals(rol)) {

            throw new SecurityException(
                    "Acceso solo para administradores"
            );

        }

        StringBuilder sql = new StringBuilder("""

                SELECT s.id_sesion,

                       CONCAT(c.nombre, ' ', c.apellido) AS cliente,

                       c.dni,

                       CONCAT(e.nombre, ' ', e.apellido) AS empleado,

                       COALESCE(g.nombre_pc, n.nombre_pc, k.codigo) AS equipo,

                       CASE

                           WHEN s.id_pc_gamer IS NOT NULL THEN 'PC Gamer'

                           WHEN s.id_pc_normal IS NOT NULL THEN 'PC Normal'

                           ELSE 'Consola'

                       END AS tipo,

                       s.hora_inicio,

                       s.hora_fin_programada,

                       s.hora_fin_real,

                       s.precio_hora_aplicado,

                       s.total_pagado,

                       s.estado,

                       t.id_ticket

                FROM sesion_alquiler s

                INNER JOIN cliente c ON c.id_cliente = s.id_cliente

                INNER JOIN empleados e ON e.id_empleado = s.id_empleado

                LEFT JOIN computadoras_gamer g ON g.id_pc_gamer = s.id_pc_gamer

                LEFT JOIN computadoras_normales n ON n.id_pc_normal = s.id_pc_normal

                LEFT JOIN consolas k ON k.id_consola = s.id_consola

                LEFT JOIN ticket t ON t.id_sesion = s.id_sesion

                WHERE s.id_cliente = ?

                """);

        List<Object> parametros = new ArrayList<>();

        parametros.add(idCliente);

        if (desde != null) {

            sql.append(" AND s.hora_inicio >= ?");

            parametros.add(Timestamp.valueOf(desde.atStartOfDay()));

        }

        if (hasta != null) {

            sql.append(" AND s.hora_inicio < ?");

            parametros.add(Timestamp.valueOf(hasta.plusDays(1).atStartOfDay()));

        }

        if (tipo != null && !"Todos".equals(tipo)) {

            sql.append("""

                     AND CASE

                         WHEN s.id_pc_gamer IS NOT NULL THEN 'PC Gamer'

                         WHEN s.id_pc_normal IS NOT NULL THEN 'PC Normal'

                         ELSE 'Consola'

                     END = ?

                    """);

            parametros.add(tipo);

        }

        if (estado != null && !"Todos".equals(estado)) {

            sql.append(" AND s.estado = ?");

            parametros.add(estado);

        }

        sql.append(" ORDER BY s.hora_inicio DESC, s.id_sesion DESC");

        List<Visita> visitas = new ArrayList<>();

        try (Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {

                ps.setObject(i + 1, parametros.get(i));

            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Timestamp inicio = rs.getTimestamp("hora_inicio");

                    Timestamp finProgramada = rs.getTimestamp("hora_fin_programada");

                    Timestamp finReal = rs.getTimestamp("hora_fin_real");

                    int idTicket = rs.getInt("id_ticket");

                    Integer ticket = rs.wasNull() ? null : idTicket;

                    visitas.add(new Visita(
                            rs.getInt("id_sesion"),
                            rs.getString("cliente"),
                            rs.getString("dni"),
                            rs.getString("empleado"),
                            rs.getString("tipo"),
                            rs.getString("equipo"),
                            inicio == null ? null : inicio.toLocalDateTime(),
                            finProgramada == null ? null : finProgramada.toLocalDateTime(),
                            finReal == null ? null : finReal.toLocalDateTime(),
                            rs.getBigDecimal("precio_hora_aplicado"),
                            rs.getBigDecimal("total_pagado"),
                            rs.getString("estado"),
                            ticket
                    ));

                }

            }

        }

        return visitas;

    }

}