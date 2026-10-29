package dao;

import config.ConexionMySQL;
import modelo.Equipo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
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

    public record Visita(int id, String cliente, String dni, String empleado,
            String tipo, String equipo, LocalDateTime inicio,
            LocalDateTime finProgramada, LocalDateTime finReal,
            BigDecimal precioHora, BigDecimal monto, String estado,
            Integer ticket) {

    }

    public record TicketInfo(int id, String cliente, String equipo,
            LocalDateTime inicio, LocalDateTime fin, BigDecimal monto) {

    }

    private record TablaEquipo(String tabla, String id, String sesion) {

    }

    private TablaEquipo tabla(String tipo) {
        return switch (tipo) {
            case "PC Gamer" ->
                new TablaEquipo("computadoras_gamer", "id_pc_gamer", "id_pc_gamer");
            case "PC Normal" ->
                new TablaEquipo("computadoras_normales", "id_pc_normal", "id_pc_normal");
            case "Consola" ->
                new TablaEquipo("consolas", "id_consola", "id_consola");
            default ->
                throw new IllegalArgumentException("Tipo de equipo desconocido");
        };
    }

    public List<Cliente> listarClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(
                "SELECT id_cliente, nombre, apellido, dni FROM cliente ORDER BY nombre, apellido"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getInt(1),
                        rs.getString(2) + " " + rs.getString(3), rs.getString(4)));
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
                WHERE LOCATE(LOWER(?), LOWER(CONCAT(nombre,' ',apellido))) > 0
                   OR LOCATE(?, dni) > 0
                   OR LOCATE(LOWER(?), LOWER(COALESCE(correo,''))) > 0
                ORDER BY nombre, apellido LIMIT 8
                """;
        try (Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            String texto = criterio.trim();
            ps.setString(1, texto);
            ps.setString(2, texto);
            ps.setString(3, texto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(new Cliente(rs.getInt(1),
                            rs.getString(2) + " " + rs.getString(3), rs.getString(4)));
                }
            }
        }
        return clientes;
    }

    public void iniciar(int idCliente, int idEmpleado, Equipo equipo,
            LocalDateTime finProgramada) throws SQLException {
        TablaEquipo t = tabla(equipo.getTipo());
        try (Connection cn = ConexionMySQL.conectar()) {
            cn.setAutoCommit(false);
            try {
                int idTarifa;
                String estado;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT estado, id_tarifa FROM " + t.tabla + " WHERE " + t.id + "=? FOR UPDATE")) {
                    ps.setInt(1, equipo.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Equipo no encontrado");
                        }
                        estado = rs.getString(1);
                        idTarifa = rs.getInt(2);
                    }
                }
                if (!"Disponible".equals(estado)) {
                    throw new SQLException("El equipo ya no está disponible");
                }
                BigDecimal precio;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT precio_hora FROM tarifa WHERE id_tarifa=?")) {
                    ps.setInt(1, idTarifa);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Tarifa no encontrada");
                        }
                        precio = rs.getBigDecimal(1);
                    }
                }
                LocalDateTime inicio = LocalDateTime.now();
                if (!finProgramada.isAfter(inicio)) {
                    throw new SQLException("La hora de fin debe ser posterior al inicio");
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO sesion_alquiler (id_cliente,id_empleado," + t.sesion
                        + ",hora_inicio,hora_fin_programada,precio_hora_aplicado,estado)"
                        + " VALUES (?,?,?,?,?,?,'ACTIVA')")) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, idEmpleado);
                    ps.setInt(3, equipo.getId());
                    ps.setTimestamp(4, Timestamp.valueOf(inicio));
                    ps.setTimestamp(5, Timestamp.valueOf(finProgramada));
                    ps.setBigDecimal(6, precio);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE " + t.tabla + " SET estado='Ocupada' WHERE " + t.id + "=?")) {
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

    public void terminar(String rol, int idSesion, boolean cancelar) throws SQLException {
        if (!"ADMIN".equals(rol)) {
            throw new SecurityException("Acceso solo para administradores");
        }
        try (Connection cn = ConexionMySQL.conectar()) {
            cn.setAutoCommit(false);
            try {
                int idEquipo;
                String tipo;
                LocalDateTime inicio;
                LocalDateTime finProgramada;
                BigDecimal precio;
                try (PreparedStatement ps = cn.prepareStatement("""
                        SELECT id_pc_gamer,id_pc_normal,id_consola,hora_inicio,
                               hora_fin_programada,precio_hora_aplicado,estado
                        FROM sesion_alquiler WHERE id_sesion=? FOR UPDATE
                        """)) {
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
                        finProgramada = rs.getTimestamp("hora_fin_programada").toLocalDateTime();
                        precio = rs.getBigDecimal("precio_hora_aplicado");
                    }
                }
                TablaEquipo t = tabla(tipo);
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT " + t.id + " FROM " + t.tabla + " WHERE " + t.id + "=? FOR UPDATE")) {
                    ps.setInt(1, idEquipo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Equipo no encontrado");
                        }
                    }
                }
                LocalDateTime fin = LocalDateTime.now();
                long segundos = Math.max(0, Duration.between(inicio,
                        cancelar ? finProgramada : fin).getSeconds());
                BigDecimal monto = precio.multiply(BigDecimal.valueOf(segundos))
                        .divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE sesion_alquiler SET hora_fin_real=?, total_pagado=?, estado=? WHERE id_sesion=?")) {
                    ps.setTimestamp(1, Timestamp.valueOf(fin));
                    ps.setBigDecimal(2, monto);
                    ps.setString(3, cancelar ? "CANCELADA" : "FINALIZADA");
                    ps.setInt(4, idSesion);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO ticket (id_sesion,monto_alquiler) VALUES (?,?)")) {
                    ps.setInt(1, idSesion);
                    ps.setBigDecimal(2, monto);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE " + t.tabla + " SET estado='Disponible' WHERE " + t.id + "=?")) {
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
                SELECT t.id_ticket,c.nombre,c.apellido,
                       COALESCE(g.nombre_pc,n.nombre_pc,k.codigo) equipo,
                       s.hora_inicio,s.hora_fin_real,t.monto_alquiler
                FROM ticket t
                JOIN sesion_alquiler s ON s.id_sesion=t.id_sesion
                JOIN cliente c ON c.id_cliente=s.id_cliente
                LEFT JOIN computadoras_gamer g ON g.id_pc_gamer=s.id_pc_gamer
                LEFT JOIN computadoras_normales n ON n.id_pc_normal=s.id_pc_normal
                LEFT JOIN consolas k ON k.id_consola=s.id_consola
                ORDER BY t.id_ticket DESC
                """;
        try (Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tickets.add(new TicketInfo(rs.getInt(1),
                        rs.getString(2) + " " + rs.getString(3), rs.getString(4),
                        rs.getTimestamp(5).toLocalDateTime(),
                        rs.getTimestamp(6).toLocalDateTime(), rs.getBigDecimal(7)));
            }
        }
        return tickets;
    }

    public int contarTicketsHistoricos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ticket";
        try (Connection cn = ConexionMySQL.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<TicketHistorial> listarHistorialTickets(Integer idCliente)
            throws SQLException {
        List<TicketHistorial> tickets = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT t.id_ticket, s.id_sesion,
                   CONCAT(c.nombre, ' ', c.apellido) AS cliente,
                   c.dni,
                   COALESCE(g.nombre_pc, n.nombre_pc, k.codigo) AS equipo,
                   CASE
                       WHEN s.id_pc_gamer IS NOT NULL THEN 'PC Gamer'
                       WHEN s.id_pc_normal IS NOT NULL THEN 'PC Normal'
                       ELSE 'Consola'
                   END AS tipo,
                   CONCAT(e.nombre, ' ', e.apellido) AS empleado,
                   s.hora_inicio, s.hora_fin_real, t.monto_alquiler
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

        try (Connection cn = ConexionMySQL.conectar();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            if (idCliente != null) {
                ps.setInt(1, idCliente);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp inicio = rs.getTimestamp("hora_inicio");
                    Timestamp fin = rs.getTimestamp("hora_fin_real");
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

    public List<Visita> listar(String rol, int idCliente, LocalDate desde,
            LocalDate hasta, String tipo, String estado) throws SQLException {
        if (!"ADMIN".equals(rol)) {
            throw new SecurityException("Acceso solo para administradores");
        }
        StringBuilder sql = new StringBuilder("""
                SELECT s.id_sesion, c.nombre, c.apellido, c.dni,
                       e.nombre AS empleado_nombre, e.apellido AS empleado_apellido,
                       COALESCE(g.nombre_pc,n.nombre_pc,k.codigo) AS equipo,
                       CASE WHEN s.id_pc_gamer IS NOT NULL THEN 'PC Gamer'
                            WHEN s.id_pc_normal IS NOT NULL THEN 'PC Normal'
                            ELSE 'Consola' END AS tipo,
                       s.hora_inicio,s.hora_fin_programada,s.hora_fin_real,
                       s.precio_hora_aplicado,s.total_pagado,s.estado,t.id_ticket
                FROM sesion_alquiler s
                JOIN cliente c ON c.id_cliente=s.id_cliente
                JOIN empleados e ON e.id_empleado=s.id_empleado
                LEFT JOIN computadoras_gamer g ON g.id_pc_gamer=s.id_pc_gamer
                LEFT JOIN computadoras_normales n ON n.id_pc_normal=s.id_pc_normal
                LEFT JOIN consolas k ON k.id_consola=s.id_consola
                LEFT JOIN ticket t ON t.id_sesion=s.id_sesion
                WHERE s.id_cliente=?
                """);
        List<Object> parametros = new ArrayList<>();
        parametros.add(idCliente);
        if (desde != null) {
            sql.append(" AND s.hora_inicio>=?");
            parametros.add(Timestamp.valueOf(desde.atStartOfDay()));
        }
        if (hasta != null) {
            sql.append(" AND s.hora_inicio<?");
            parametros.add(Timestamp.valueOf(hasta.plusDays(1).atStartOfDay()));
        }
        if (tipo != null && !"Todos".equals(tipo)) {
            sql.append(" AND CASE WHEN s.id_pc_gamer IS NOT NULL THEN 'PC Gamer' WHEN s.id_pc_normal IS NOT NULL THEN 'PC Normal' ELSE 'Consola' END=?");
            parametros.add(tipo);
        }
        if (estado != null && !"Todos".equals(estado)) {
            sql.append(" AND s.estado=?");
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
                    Timestamp fin = rs.getTimestamp("hora_fin_real");
                    int ticket = rs.getInt("id_ticket");
                    Integer ticketId = rs.wasNull() ? null : ticket;
                    visitas.add(new Visita(rs.getInt("id_sesion"),
                            rs.getString("nombre") + " " + rs.getString("apellido"),
                            rs.getString("dni"),
                            rs.getString("empleado_nombre") + " " + rs.getString("empleado_apellido"),
                            rs.getString("tipo"), rs.getString("equipo"),
                            rs.getTimestamp("hora_inicio").toLocalDateTime(),
                            rs.getTimestamp("hora_fin_programada").toLocalDateTime(),
                            fin == null ? null : fin.toLocalDateTime(),
                            rs.getBigDecimal("precio_hora_aplicado"),
                            rs.getBigDecimal("total_pagado"), rs.getString("estado"),
                            ticketId));
                }
            }
        }
        return visitas;
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
            BigDecimal monto
            ) {

    }
}
