package modelo;

import dao.SesionDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class HistorialClientesModel {

    private final SesionDAO dao = new SesionDAO();

    public int contarTickets() throws SQLException {
        return dao.contarTicketsHistoricos();
    }

    public List<SesionDAO.TicketHistorial> listarTodos() throws SQLException {
        return dao.listarHistorialTickets(null);
    }

    public List<SesionDAO.TicketHistorial> buscarPorCliente(int idCliente)
            throws SQLException {
        return dao.listarHistorialTickets(idCliente);
    }

    public List<SesionDAO.Cliente> buscarClientes(String texto)
            throws SQLException {
        return dao.buscarClientes(texto);
    }

    public List<SesionDAO.Visita> listar(String rol, int idCliente, LocalDate desde,
            LocalDate hasta, String tipo, String estado) throws SQLException {
        return dao.listar(rol, idCliente, desde, hasta, tipo, estado);
    }

    public void terminar(String rol, int idSesion, boolean cancelar) throws SQLException {
        dao.terminar(rol, idSesion, cancelar);
    }
}
