package dao;

import config.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public String autenticar(String correo, String clave)
            throws SQLException {

        String sql = """
            SELECT u.rol, u.password_hash
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

                    // Comparación temporal para tu contraseña
                    // actual almacenada como texto plano.
                    if (claveGuardada.equals(clave)) {
                        return rs.getString("rol");
                    }
                }
            }
        }

        return null;
    }
}