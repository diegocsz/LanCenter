
package dao;

import config.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClienteDAO {

    public boolean registrarCliente(String dni, String nombre,
            String apellido, String telefono, String correo)
            throws SQLException {

        String sql = "INSERT INTO cliente "
                + "(dni, nombre, apellido, telefono, correo) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = ConexionMySQL.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, telefono);
            ps.setString(5, correo);

            return ps.executeUpdate() > 0;
        }
    }
}
