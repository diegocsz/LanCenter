package dao;

import config.ConexionMySQL;
import modelo.Ticket;

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
                s.hora_fin_real,
                t.fecha_ticket,
                t.monto_alquiler
            FROM ticket t
            INNER JOIN sesion_alquiler s
                ON t.id_sesion = s.id_sesion
            INNER JOIN cliente c
                ON s.id_cliente = c.id_cliente
            ORDER BY
                t.fecha_ticket DESC,
                t.hora_emision DESC,
                t.id_ticket DESC
            """;

        try (
            Connection cn = ConexionMySQL.conectar();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

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
}