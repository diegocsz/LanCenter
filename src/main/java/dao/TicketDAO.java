package dao;

import config.ConexionMySQL;
import modelo.Ticket;
import modelo.TicketInfo;
import modelo.TicketHistorial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public List<Ticket> listarTickets() throws SQLException {

        List<Ticket> tickets = new ArrayList<>();

        String sql = """
    SELECT
        t.id_ticket,
        CONCAT(c.nombre, ' ', c.apellido) AS cliente,
        s.hora_inicio,
        COALESCE(s.hora_fin_real, s.hora_fin_programada) AS hora_fin_real,
        t.fecha_ticket,
        t.monto_alquiler
    FROM ticket t
    INNER JOIN sesion_alquiler s
        ON t.id_sesion = s.id_sesion
    INNER JOIN cliente c
        ON s.id_cliente = c.id_cliente
    WHERE t.fecha_ticket = CURDATE()
    ORDER BY
        t.fecha_ticket DESC,
        t.hora_emision DESC,
        t.id_ticket DESC
    """;

        try (
                Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Timestamp inicio = rs.getTimestamp("hora_inicio");
                Timestamp fin = rs.getTimestamp("hora_fin_real");

                Ticket ticket = new Ticket(
                        rs.getInt("id_ticket"),
                        rs.getString("cliente"),
                        inicio.toLocalDateTime(),
                        fin.toLocalDateTime(),
                        rs.getDate("fecha_ticket").toLocalDate(),
                        rs.getBigDecimal("monto_alquiler")
                );

                tickets.add(ticket);
            }
        }

        return tickets;
    }

    public List<TicketInfo> listarTicketsInfo() throws SQLException {
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
}
