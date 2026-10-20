
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMySQL {

    private static final String URL =
            "jdbc:mysql://localhost:3306/lan_center";

    private static final String USUARIO = "root";

    // Contraseña directa de MySQL
    private static final String PASSWORD = "diego";

    public static Connection conectar() throws SQLException {
        if (PASSWORD == null || PASSWORD.isEmpty()) {
            throw new SQLException(
                    "No se ha configurado la contraseña de MySQL."
            );
        }

        return DriverManager.getConnection(
                URL, USUARIO, PASSWORD
        );
    }

    public static boolean verificarConexion() {
        try (Connection cn = conectar()) {
            return cn.isValid(3);
        } catch (SQLException e) {
            System.err.println(
                    "Error de conexión MySQL: " + e.getMessage()
            );
            return false;
        }
    }
}
