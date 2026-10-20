
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMySQL {

    private static final String URL =
            "jdbc:mysql://localhost:3306/lan_center";

    private static final String USUARIO = "root";

    private static final String PASSWORD =
        System.getenv("LANCENTER_DB_PASSWORD");

    public static Connection conectar() throws SQLException {

        if (PASSWORD == null || PASSWORD.isEmpty()) {
            throw new SQLException(
                "No se ha configurado la contrasena de MySQL"
            );
        }

        return DriverManager.getConnection(
                URL, USUARIO, PASSWORD
        );
    }

    public static void main(String[] args) {

        try (Connection cn = conectar()) {
            System.out.println("Conexion exitosa con MySQL");
            System.out.println("Base de datos: lan_center");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
