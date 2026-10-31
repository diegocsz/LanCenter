package controlador;

import modelo.ClienteResumen;
import modelo.Visita;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import modelo.HistorialClientesModel;

public class HistorialClientesController {
    private final HistorialClientesModel modelo;

    public HistorialClientesController(HistorialClientesModel modelo) {
        this.modelo = modelo;
    }

    public List<ClienteResumen> buscarClientes(String texto) throws SQLException {
        return modelo.buscarClientes(texto);
    }

    public List<Visita> listar(String rol, int idCliente, LocalDate desde,
            LocalDate hasta, String tipo, String estado) throws SQLException {
        return modelo.listar(rol, idCliente, desde, hasta, tipo, estado);
    }

    public void terminar(String rol, int idSesion, boolean cancelar) throws SQLException {
        modelo.terminar(rol, idSesion, cancelar);
    }
}
