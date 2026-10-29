package dao;

import config.ConexionMySQL;
import modelo.MetricasTickets;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MetricasTicketsDAO {

    public MetricasTickets obtenerMetricasDelDia() throws SQLException {

        String sql = """
            SELECT
                COALESCE(
                    SUM(t.monto_alquiler),
                    0
                ) AS monto_acumulado,

                COUNT(t.id_ticket) AS sesiones_alquiladas,

                COALESCE(
                    SUM(
                        CASE
                            WHEN s.id_pc_normal IS NOT NULL
                            THEN 1 ELSE 0
                        END
                    ),
                    0
                ) AS equipos_normales,

                COALESCE(
                    SUM(
                        CASE
                            WHEN s.id_pc_gamer IS NOT NULL
                            THEN 1 ELSE 0
                        END
                    ),
                    0
                ) AS equipos_gamer,

                COALESCE(
                    SUM(
                        CASE
                            WHEN s.id_consola IS NOT NULL
                            THEN 1 ELSE 0
                        END
                    ),
                    0
                ) AS consolas,

                COALESCE(
                    SUM(
                        TIMESTAMPDIFF(
                            MINUTE,
                            s.hora_inicio,
                            COALESCE(
                                s.hora_fin_real,
                                s.hora_fin_programada
                            )
                        )
                    ),
                    0
                ) AS minutos_alquilados,

                COALESCE(
                    ROUND(AVG(t.monto_alquiler), 2),
                    0
                ) AS promedio_ticket,

                COALESCE(
                    ROUND(
                        AVG(
                            TIMESTAMPDIFF(
                                MINUTE,
                                s.hora_inicio,
                                COALESCE(
                                    s.hora_fin_real,
                                    s.hora_fin_programada
                                )
                            )
                        ),
                        0
                    ),
                    0
                ) AS duracion_promedio,

                COALESCE(
                    MAX(t.monto_alquiler),
                    0
                ) AS ticket_mayor_monto

            FROM ticket t

            INNER JOIN sesion_alquiler s
                ON t.id_sesion = s.id_sesion

            WHERE t.fecha_ticket = CURDATE()
            """;

        try (
            Connection cn = ConexionMySQL.conectar();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {

                return new MetricasTickets(
                    rs.getBigDecimal("monto_acumulado"),
                    rs.getInt("sesiones_alquiladas"),
                    rs.getInt("equipos_normales"),
                    rs.getInt("equipos_gamer"),
                    rs.getInt("consolas"),
                    rs.getLong("minutos_alquilados"),
                    rs.getBigDecimal("promedio_ticket"),
                    rs.getLong("duracion_promedio"),
                    rs.getBigDecimal("ticket_mayor_monto")
                );
            }
        }

        return new MetricasTickets(
            BigDecimal.ZERO,
            0,
            0,
            0,
            0,
            0,
            BigDecimal.ZERO,
            0,
            BigDecimal.ZERO
        );
    }
}