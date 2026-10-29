package dao;

import config.ConexionMySQL;
import modelo.Equipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    public List<Equipo> listarEquipos() throws SQLException {

        List<Equipo> equipos = new ArrayList<>();

        String sql = """
            SELECT e.id, e.nombre, e.tipo, e.estado,
                   e.precio_hora, s.hora_fin_programada
            FROM (
                SELECT g.id_pc_gamer AS id,
                       g.nombre_pc AS nombre,
                       'PC Gamer' AS tipo,
                       g.estado,
                       t.precio_hora
                FROM computadoras_gamer g
                JOIN tarifa t ON g.id_tarifa = t.id_tarifa

                UNION ALL

                SELECT n.id_pc_normal,
                       n.nombre_pc,
                       'PC Normal',
                       n.estado,
                       t.precio_hora
                FROM computadoras_normales n
                JOIN tarifa t ON n.id_tarifa = t.id_tarifa

                UNION ALL

                SELECT c.id_consola,
                       c.codigo,
                       'Consola',
                       c.estado,
                       t.precio_hora
                FROM consolas c
                JOIN tarifa t ON c.id_tarifa = t.id_tarifa
            ) e
            LEFT JOIN (
                SELECT 'PC Gamer' AS tipo,
                       id_pc_gamer AS id,
                       MAX(hora_fin_programada) AS hora_fin_programada
                FROM sesion_alquiler
                WHERE estado = 'ACTIVA'
                  AND id_pc_gamer IS NOT NULL
                GROUP BY id_pc_gamer

                UNION ALL

                SELECT 'PC Normal',
                       id_pc_normal,
                       MAX(hora_fin_programada)
                FROM sesion_alquiler
                WHERE estado = 'ACTIVA'
                  AND id_pc_normal IS NOT NULL
                GROUP BY id_pc_normal

                UNION ALL

                SELECT 'Consola',
                       id_consola,
                       MAX(hora_fin_programada)
                FROM sesion_alquiler
                WHERE estado = 'ACTIVA'
                  AND id_consola IS NOT NULL
                GROUP BY id_consola
            ) s ON s.tipo = e.tipo AND s.id = e.id
            ORDER BY FIELD(e.tipo, 'PC Gamer',
                           'PC Normal', 'Consola'), e.id
            """;

        try (Connection cn = ConexionMySQL.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Timestamp fin = rs.getTimestamp(
                    "hora_fin_programada"
                );

                Equipo equipo = new Equipo(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("tipo"),
                    rs.getString("estado"),
                    rs.getBigDecimal("precio_hora"),
                    fin != null ? fin.toLocalDateTime() : null
                );

                equipos.add(equipo);
            }
        }

        return equipos;
    }

    public boolean cambiarEstado(Equipo equipo, String nuevoEstado)
            throws SQLException {

        String tabla;
        String columnaId;

        switch (equipo.getTipo()) {
            case "PC Gamer":
                tabla = "computadoras_gamer";
                columnaId = "id_pc_gamer";
                break;

            case "PC Normal":
                tabla = "computadoras_normales";
                columnaId = "id_pc_normal";
                break;

            case "Consola":
                tabla = "consolas";
                columnaId = "id_consola";
                break;

            default:
                throw new IllegalArgumentException(
                    "Tipo de equipo desconocido"
                );
        }

        String sql = "UPDATE " + tabla
                   + " SET estado = ? WHERE "
                   + columnaId + " = ?";

        try (Connection cn = ConexionMySQL.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, equipo.getId());

            return ps.executeUpdate() > 0;
        }
    }
}

