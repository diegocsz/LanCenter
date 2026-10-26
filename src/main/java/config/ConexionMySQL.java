
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConexionMySQL {

    private static final String URL =
            "jdbc:mysql://localhost:3306/lan_center";

    private static final String USUARIO = "root";

    // Valor existente del proyecto. Cada equipo puede sobrescribirlo localmente.
    private static final String PASSWORD = "diego";

    public static Connection conectar() throws SQLException {
        String password = PASSWORD;
        String url = URL;
        String usuario = USUARIO;
        Path configuracionLocal = Path.of(
                System.getProperty("user.home"), ".lancenter", "db.properties");
        if (Files.exists(configuracionLocal)) {
            Properties propiedades = new Properties();
            try (var entrada = Files.newInputStream(configuracionLocal)) {
                propiedades.load(entrada);
            } catch (IOException e) {
                throw new SQLException("No se pudo leer la configuración local de MySQL.", e);
            }
            password = propiedades.getProperty("password", PASSWORD);
            url = propiedades.getProperty("url", URL);
            usuario = propiedades.getProperty("user", USUARIO);
        }
        return DriverManager.getConnection(
                url, usuario, password
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
