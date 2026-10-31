package modelo;

import dao.SesionDAO;
import dao.ClienteDAO;
import dao.TicketDAO;
import dao.VisitaDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class HistorialClientesModel {

    private final SesionDAO sesionDAO = new SesionDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final VisitaDAO visitaDAO = new VisitaDAO();

    public int contarTickets() throws SQLException {
        return ticketDAO.contarTicketsHistoricos();
    }

    public List<TicketHistorial> listarTodos() throws SQLException {
        return ticketDAO.listarHistorialTickets(null);
    }

    public List<TicketHistorial> buscarPorCliente(int idCliente)
            throws SQLException {
        return ticketDAO.listarHistorialTickets(idCliente);
    }

    public List<ClienteResumen> buscarClientes(String texto)
            throws SQLException {
        return clienteDAO.buscarClientes(texto);
    }

    public List<Visita> listar(String rol, int idCliente, LocalDate desde,
            LocalDate hasta, String tipo, String estado) throws SQLException {
        return visitaDAO.listar(rol, idCliente, desde, hasta, tipo, estado);
    }

    public void terminar(String rol, int idSesion, boolean cancelar) throws SQLException {
        sesionDAO.terminar(rol, idSesion, cancelar);
    }
}
