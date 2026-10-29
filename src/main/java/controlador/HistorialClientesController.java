package controlador;

import dao.SesionDAO;
import modelo.HistorialClientesModel;
import vista.PanelHistorialClientes;
import java.sql.SQLException;
import java.util.List;

public class HistorialClientesController {

    private final HistorialClientesModel modelo;
    private final PanelHistorialClientes vista;

    public HistorialClientesController(HistorialClientesModel modelo,
                                      PanelHistorialClientes vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    public void iniciar() {
        try {
            vista.mostrarTotalTickets(modelo.contarTickets());
            vista.mostrarTickets(modelo.listarTodos());
        } catch (SQLException ex) {
            vista.mostrarError("No se pudo cargar el historial", ex.getMessage());
        }
    }

    public void buscarClientes(String texto) {
        try {
            vista.mostrarSugerencias(modelo.buscarClientes(texto));
        } catch (SQLException ex) {
            vista.mostrarError("No se pudo buscar el cliente", ex.getMessage());
        }
    }

    public void seleccionarCliente(SesionDAO.Cliente cliente) {
        try {
            vista.establecerCliente(cliente);
            vista.mostrarTickets(modelo.buscarPorCliente(cliente.id()));
        } catch (SQLException ex) {
            vista.mostrarError("No se pudo consultar el historial", ex.getMessage());
        }
    }

    public void mostrarTodos() {
        try {
            vista.limpiarBusqueda();
            vista.mostrarTickets(modelo.listarTodos());
        } catch (SQLException ex) {
            vista.mostrarError("No se pudo cargar el historial", ex.getMessage());
        }
    }
}
