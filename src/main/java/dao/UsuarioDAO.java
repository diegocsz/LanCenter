package dao;

import config.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public record UsuarioAutenticado(int idEmpleado, String rol) {}

    public UsuarioAutenticado autenticar(String correo, String clave)
            throws SQLException {

        String sql = """
            SELECT u.id_empleado, u.rol, u.password_hash
            FROM usuarios u
            INNER JOIN empleados e
                ON u.id_empleado = e.id_empleado
            WHERE u.correo = ?
              AND u.activo = TRUE
            """;

        try (Connection cn = ConexionMySQL.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    String claveGuardada =
                            rs.getString("password_hash");

                    if (claveGuardada.equals(clave)) {
                        return new UsuarioAutenticado(
                                rs.getInt("id_empleado"), rs.getString("rol"));
                    }
                }
            }
        }

        return null;
    }
}
