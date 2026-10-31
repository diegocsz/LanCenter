
package dao;

import config.ConexionMySQL;
import modelo.ClienteResumen;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
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

    public List<ClienteResumen> listarClientes() throws SQLException {
        List<ClienteResumen> clientes = new ArrayList<>();
        String sql = """
                SELECT id_cliente, nombre, apellido, dni
                FROM cliente
                ORDER BY nombre, apellido
                """;
        try (Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(new ClienteResumen(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre") + " " + rs.getString("apellido"),
                        rs.getString("dni")
                ));
            }
        }
        return clientes;
    }

    public List<ClienteResumen> buscarClientes(String criterio) throws SQLException {
        if (criterio == null || criterio.isBlank()) {
            return List.of();
        }
        List<ClienteResumen> clientes = new ArrayList<>();
        String sql = """
                SELECT id_cliente, nombre, apellido, dni
                FROM cliente
                WHERE LOCATE(LOWER(?), LOWER(CONCAT(nombre, ' ', apellido))) > 0
                   OR LOCATE(?, dni) > 0
                   OR LOCATE(LOWER(?), LOWER(COALESCE(correo, ''))) > 0
                ORDER BY nombre, apellido
                LIMIT 8
                """;
        String texto = criterio.trim();
        try (Connection cn = ConexionMySQL.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, texto);
            ps.setString(2, texto);
            ps.setString(3, texto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(new ClienteResumen(
                            rs.getInt("id_cliente"),
                            rs.getString("nombre") + " " + rs.getString("apellido"),
                            rs.getString("dni")
                    ));
                }
            }
        }
        return clientes;
    }
}
