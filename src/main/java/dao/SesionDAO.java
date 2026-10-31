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
import java.time.LocalDateTime;

public class SesionDAO {
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
                // Si el trigger ya creó el ticket, actualiza el monto sin duplicarlo.
                String sqlTicket = """
                        INSERT INTO ticket (id_sesion, monto_alquiler)
                        VALUES (?, ?)
                        ON DUPLICATE KEY UPDATE monto_alquiler = ?
                        """;
                try (PreparedStatement ps = cn.prepareStatement(sqlTicket)) {
                    ps.setInt(1, idSesion);
                    ps.setBigDecimal(2, monto);
                    ps.setBigDecimal(3, monto);
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
}
