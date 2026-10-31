package dao;

import config.ConexionMySQL;
import modelo.Visita;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VisitaDAO {

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
