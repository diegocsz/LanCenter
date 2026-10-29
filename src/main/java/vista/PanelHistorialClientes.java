package vista;

import controlador.HistorialClientesController;
import dao.SesionDAO;
import dao.UsuarioDAO;
import modelo.HistorialClientesModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class PanelHistorialClientes extends JPanel {

    private static final Color FONDO = new Color(245, 247, 250);
    private static final Color TEXTO = new Color(35, 43, 55);
    private static final Color SECUNDARIO = new Color(110, 120, 135);
    private static final Color BORDE = new Color(225, 230, 237);
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final HistorialClientesModel modelo;
    private final HistorialClientesController controller;
    private final JTextField buscar = new JTextField();
    private final JLabel totalTickets = new JLabel("0");
    private final JLabel estado = new JLabel("Todos los tickets");
    private final DefaultListModel<SesionDAO.Cliente> sugerenciasModelo =
            new DefaultListModel<>();
    private final JList<SesionDAO.Cliente> sugerencias =
            new JList<>(sugerenciasModelo);
    private final JPanel panelSugerencias = new JPanel(new BorderLayout());
    private final DefaultTableModel tablaModelo = new DefaultTableModel(
            new String[]{"Ticket", "Sesión", "Cliente", "DNI", "Equipo",
                "Tipo", "Empleado", "Inicio", "Fin", "Monto"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(tablaModelo);
    private final Timer temporizadorBusqueda = new Timer(300, e -> buscarClientes());
    private boolean escribiendoSeleccion;

    public PanelHistorialClientes(UsuarioDAO.UsuarioAutenticado usuario) {
        if (usuario == null || !"ADMIN".equals(usuario.rol())) {
            throw new SecurityException("Solo los administradores pueden consultar el historial.");
        }

        modelo = new HistorialClientesModel();
        controller = new HistorialClientesController(modelo, this);

        setLayout(new BorderLayout(0, 14));
        setBackground(FONDO);
        setBorder(new EmptyBorder(22, 24, 22, 24));
        construirInterfaz();
        configurarEventos();
        temporizadorBusqueda.setRepeats(false);
        controller.iniciar();
    }

    private void construirInterfaz() {
        JPanel encabezado = new JPanel(new BorderLayout(0, 5));
        encabezado.setOpaque(false);
        JLabel titulo = new JLabel("Historial de tickets");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXTO);
        JLabel subtitulo = new JLabel("Consulta los tickets registrados y busca por cliente.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(SECUNDARIO);
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(subtitulo, BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);

        JPanel superior = new JPanel(new BorderLayout(12, 0));
        superior.setBackground(Color.WHITE);
        superior.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE), new EmptyBorder(14, 16, 14, 16)));
        JPanel busqueda = new JPanel(new BorderLayout(8, 0));
        busqueda.setOpaque(false);
        buscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        buscar.setPreferredSize(new Dimension(260, 36));
        buscar.setToolTipText("Escribe el nombre o DNI del cliente");
        JButton botonBuscar = new JButton("Buscar cliente");
        JButton botonTodos = new JButton("Mostrar todos");
        busqueda.add(buscar, BorderLayout.CENTER);
        JPanel botones = new JPanel(new GridLayout(1, 2, 8, 0));
        botones.setOpaque(false);
        botones.add(botonBuscar);
        botones.add(botonTodos);
        busqueda.add(botones, BorderLayout.EAST);
        superior.add(busqueda, BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new BorderLayout(8, 0));
        totalPanel.setOpaque(false);
        JLabel totalEtiqueta = new JLabel("TOTAL HISTÓRICO DE TICKETS");
        totalEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, 10));
        totalEtiqueta.setForeground(SECUNDARIO);
        totalTickets.setFont(new Font("Segoe UI", Font.BOLD, 23));
        totalTickets.setForeground(TEXTO);
        totalPanel.add(totalEtiqueta, BorderLayout.NORTH);
        totalPanel.add(totalTickets, BorderLayout.CENTER);
        superior.add(totalPanel, BorderLayout.EAST);

        panelSugerencias.setBorder(BorderFactory.createLineBorder(BORDE));
        sugerencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sugerencias.setVisibleRowCount(5);
        panelSugerencias.add(new JScrollPane(sugerencias), BorderLayout.CENTER);
        panelSugerencias.setVisible(false);

        JPanel bloqueBusqueda = new JPanel(new BorderLayout(0, 5));
        bloqueBusqueda.setOpaque(false);
        bloqueBusqueda.add(superior, BorderLayout.NORTH);
        bloqueBusqueda.add(panelSugerencias, BorderLayout.CENTER);
        centro.add(bloqueBusqueda, BorderLayout.NORTH);

        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(30);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 34));
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        centro.add(scroll, BorderLayout.CENTER);
        estado.setForeground(SECUNDARIO);
        estado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        centro.add(estado, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        botonBuscar.addActionListener(e -> buscarClientes());
        botonTodos.addActionListener(e -> controller.mostrarTodos());
    }

    private void configurarEventos() {
        buscar.getDocument().addDocumentListener(new DocumentListener() {
            private void cambio() {
                if (escribiendoSeleccion) return;
                temporizadorBusqueda.restart();
                if (buscar.getText().isBlank()) {
                    panelSugerencias.setVisible(false);
                    sugerenciasModelo.clear();
                    controller.mostrarTodos();
                }
            }
            @Override public void insertUpdate(DocumentEvent e) { cambio(); }
            @Override public void removeUpdate(DocumentEvent e) { cambio(); }
            @Override public void changedUpdate(DocumentEvent e) { cambio(); }
        });

        sugerencias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SesionDAO.Cliente cliente = sugerencias.getSelectedValue();
                if (cliente != null) controller.seleccionarCliente(cliente);
            }
        });
    }

    private void buscarClientes() {
        String texto = buscar.getText().trim();
        if (texto.isBlank()) {
            panelSugerencias.setVisible(false);
            return;
        }
        controller.buscarClientes(texto);
    }

    public void mostrarTotalTickets(int total) {
        totalTickets.setText(String.valueOf(total));
    }

    public void mostrarSugerencias(List<SesionDAO.Cliente> clientes) {
        sugerenciasModelo.clear();
        for (SesionDAO.Cliente cliente : clientes) sugerenciasModelo.addElement(cliente);
        panelSugerencias.setVisible(!clientes.isEmpty());
        if (clientes.isEmpty()) estado.setText("No se encontraron clientes.");
        revalidate();
        repaint();
    }

    public void establecerCliente(SesionDAO.Cliente cliente) {
        escribiendoSeleccion = true;
        buscar.setText(cliente.toString());
        escribiendoSeleccion = false;
        sugerenciasModelo.clear();
        panelSugerencias.setVisible(false);
        estado.setText("Tickets del cliente: " + cliente.nombre() + " (DNI " + cliente.dni() + ")");
    }

    public void mostrarTickets(List<SesionDAO.TicketHistorial> tickets) {
        tablaModelo.setRowCount(0);
        for (SesionDAO.TicketHistorial t : tickets) {
            tablaModelo.addRow(new Object[]{
                t.idTicket(), t.idSesion(), t.cliente(), t.dni(), t.equipo(),
                t.tipo(), t.empleado(), formato(t.inicio()), formato(t.fin()),
                t.monto() == null ? "—" : "S/ " + t.monto().toPlainString()
            });
        }
        if (tickets.isEmpty()) estado.setText("No hay tickets para mostrar.");
        else if (estado.getText().equals("Todos los tickets") || estado.getText().isBlank())
            estado.setText(tickets.size() + " tickets mostrados");
    }

    public void limpiarBusqueda() {
        escribiendoSeleccion = true;
        buscar.setText("");
        escribiendoSeleccion = false;
        sugerenciasModelo.clear();
        panelSugerencias.setVisible(false);
        estado.setText("Todos los tickets");
    }

    public void mostrarError(String titulo, String detalle) {
        JOptionPane.showMessageDialog(this,
                detalle == null ? "Error desconocido." : detalle,
                titulo, JOptionPane.ERROR_MESSAGE);
    }

    private String formato(LocalDateTime fecha) {
        return fecha == null ? "—" : fecha.format(FORMATO);
    }
}
