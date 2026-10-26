package com.mycompany.lancenter;

import dao.SesionDAO;
import dao.UsuarioDAO;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class PanelHistorialClientes extends JPanel {
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final UsuarioDAO.UsuarioAutenticado usuario;
    private final SesionDAO dao = new SesionDAO();
    private final JTextField buscar = new JTextField(16);
    private final DefaultListModel<SesionDAO.Cliente> modeloSugerencias =
            new DefaultListModel<>();
    private final JList<SesionDAO.Cliente> sugerencias = new JList<>(modeloSugerencias);
    private final JScrollPane listaSugerencias = new JScrollPane(sugerencias);
    private final Timer pausaBusqueda = new Timer(250, e -> mostrarSugerencias());
    private SesionDAO.Cliente clienteSeleccionado;
    private boolean seleccionandoCliente;
    private final JTextField desde = new JTextField(10);
    private final JTextField hasta = new JTextField(10);
    private final JComboBox<String> tipo = new JComboBox<>(
            new String[]{"Todos", "PC Gamer", "PC Normal", "Consola"});
    private final JComboBox<String> estado = new JComboBox<>(
            new String[]{"Todos", "ACTIVA", "FINALIZADA", "CANCELADA"});
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Cliente", "DNI", "Estado", "Equipo", "Tipo",
                    "Uso real", "Inicio", "Fin programado", "Fin real", "Tarifa/h",
                    "Total", "Ticket", "Empleado"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JLabel resumen = new JLabel();
    private final JPanel graficos = new JPanel(new GridLayout(1, 2, 20, 0));
    private final JPanel pie = new JPanel(new BorderLayout(0, 10));
    private final JButton finalizar = new JButton("Finalizar sesión activa");
    private final JButton cancelar = new JButton("Cancelar sesión activa");
    private final JLabel avisoAcciones =
            new JLabel("Estas acciones solo están disponibles para una sesión ACTIVA.");
    private List<SesionDAO.Visita> visitas = List.of();

    public PanelHistorialClientes(UsuarioDAO.UsuarioAutenticado usuario) {
        if (!"ADMIN".equals(usuario.rol())) throw new SecurityException("Solo administradores");
        this.usuario = usuario;
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(240, 240, 240));
        setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel titulo = new JLabel("HISTORIAL DE CLIENTES");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        JPanel filtros = new JPanel(new GridLayout(2, 1, 0, 2));
        filtros.setOpaque(false);
        JPanel filaFechas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        JPanel filaOpciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        filaFechas.setOpaque(false);
        filaOpciones.setOpaque(false);
        buscar.setToolTipText("Escribe nombre, DNI o correo y elige una sugerencia");
        pausaBusqueda.setRepeats(false);
        buscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { cambioBusqueda(); }
            @Override public void removeUpdate(DocumentEvent e) { cambioBusqueda(); }
            @Override public void changedUpdate(DocumentEvent e) { cambioBusqueda(); }
        });
        sugerencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sugerencias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && sugerencias.getSelectedValue() != null) {
                seleccionarCliente(sugerencias.getSelectedValue());
            }
        });
        listaSugerencias.setPreferredSize(new Dimension(0, 90));
        listaSugerencias.setVisible(false);
        desde.setToolTipText("AAAA-MM-DD");
        hasta.setToolTipText("AAAA-MM-DD");
        filaFechas.add(new JLabel("Buscar cliente:"));
        filaFechas.add(buscar);
        filaFechas.add(new JLabel("Desde:"));
        filaFechas.add(desde);
        filaFechas.add(new JLabel("Hasta:"));
        filaFechas.add(hasta);
        filaOpciones.add(new JLabel("Equipo:"));
        filaOpciones.add(tipo);
        filaOpciones.add(new JLabel("Estado:"));
        filaOpciones.add(estado);
        JButton aplicar = new JButton("Filtrar");
        aplicar.addActionListener(e -> cargar());
        JButton limpiar = new JButton("Limpiar");
        limpiar.addActionListener(e -> {
            seleccionandoCliente = true;
            buscar.setText("");
            seleccionandoCliente = false;
            clienteSeleccionado = null;
            listaSugerencias.setVisible(false);
            modeloSugerencias.clear();
            desde.setText(""); hasta.setText("");
            tipo.setSelectedIndex(0); estado.setSelectedIndex(0);
            vaciar("Busca por nombre, DNI o correo y selecciona un cliente.");
        });
        filaOpciones.add(aplicar);
        filaOpciones.add(limpiar);
        filtros.add(filaFechas);
        filtros.add(filaOpciones);
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(titulo, BorderLayout.NORTH);
        cabecera.add(filtros, BorderLayout.CENTER);
        cabecera.add(listaSugerencias, BorderLayout.SOUTH);
        add(cabecera, BorderLayout.NORTH);

        tabla.setRowHeight(26);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] anchos = {50, 160, 90, 105, 120, 100, 90, 125, 125, 125, 75, 75, 60, 150};
        for (int i = 0; i < anchos.length; i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        tabla.getSelectionModel().addListSelectionListener(e -> actualizarAcciones());
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        pie.setOpaque(false);
        resumen.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pie.add(resumen, BorderLayout.NORTH);
        graficos.setOpaque(false);
        pie.add(graficos, BorderLayout.CENTER);
        JPanel acciones = new JPanel(new BorderLayout());
        acciones.setOpaque(false);
        finalizar.addActionListener(e -> terminar(false));
        cancelar.addActionListener(e -> terminar(true));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setOpaque(false);
        botones.add(finalizar);
        botones.add(cancelar);
        avisoAcciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        acciones.add(avisoAcciones, BorderLayout.WEST);
        acciones.add(botones, BorderLayout.EAST);
        pie.add(acciones, BorderLayout.SOUTH);
        add(pie, BorderLayout.SOUTH);
        vaciar("Busca por nombre, DNI o correo y selecciona un cliente.");
    }

    private void cambioBusqueda() {
        if (seleccionandoCliente) return;
        clienteSeleccionado = null;
        vaciar("Selecciona un cliente de las sugerencias para ver su historial.");
        pausaBusqueda.stop();
        listaSugerencias.setVisible(false);
        modeloSugerencias.clear();
        if (!buscar.getText().isBlank()) pausaBusqueda.restart();
    }

    private void mostrarSugerencias() {
        if (buscar.getText().isBlank()) return;
        try {
            List<SesionDAO.Cliente> clientes = dao.buscarClientes(buscar.getText());
            modeloSugerencias.clear();
            for (SesionDAO.Cliente cliente : clientes) modeloSugerencias.addElement(cliente);
            listaSugerencias.setVisible(!clientes.isEmpty());
            revalidate();
            repaint();
            if (clientes.isEmpty()) resumen.setText("No se encontraron clientes con ese dato.");
        } catch (SQLException e) {
            listaSugerencias.setVisible(false);
            resumen.setText("No se pudieron buscar clientes: " + e.getMessage());
        }
    }

    private void seleccionarCliente(SesionDAO.Cliente cliente) {
        listaSugerencias.setVisible(false);
        seleccionandoCliente = true;
        buscar.setText(cliente.toString());
        seleccionandoCliente = false;
        clienteSeleccionado = cliente;
        cargar();
    }

    private void vaciar(String mensaje) {
        visitas = List.of();
        modelo.setRowCount(0);
        resumen.setText(mensaje);
        graficos.removeAll();
        graficos.setVisible(false);
        pie.setPreferredSize(new Dimension(0, 75));
        actualizarAcciones();
        revalidate();
        repaint();
    }

    private void actualizarAcciones() {
        int fila = tabla.getSelectedRow();
        boolean activa = fila >= 0 && fila < visitas.size()
                && "ACTIVA".equals(visitas.get(fila).estado());
        finalizar.setEnabled(activa);
        cancelar.setEnabled(activa);
        avisoAcciones.setText(activa
                ? "Sesión activa seleccionada: puedes finalizarla o cancelarla."
                : "Estas acciones solo están disponibles para una sesión ACTIVA.");
    }

    private LocalDate fecha(String texto) {
        return texto.isBlank() ? null : LocalDate.parse(texto.trim());
    }

    private void cargar() {
        if (clienteSeleccionado == null) {
            vaciar("Selecciona un cliente de las sugerencias para ver su historial.");
            return;
        }
        try {
            LocalDate inicio = fecha(desde.getText());
            LocalDate fin = fecha(hasta.getText());
            if (inicio != null && fin != null && fin.isBefore(inicio)) {
                throw new IllegalArgumentException("La fecha final debe ser posterior a la inicial.");
            }
            visitas = dao.listar(usuario.rol(), clienteSeleccionado.id(), inicio, fin,
                    (String) tipo.getSelectedItem(), (String) estado.getSelectedItem());
            modelo.setRowCount(0);
            BigDecimal total = BigDecimal.ZERO;
            int finalizadas = 0;
            int activas = 0;
            long minutosFinalizados = 0;
            long minutosReales = 0;
            for (SesionDAO.Visita v : visitas) {
                long minutosVisita = v.finReal() == null ? 0
                        : Duration.between(v.inicio(), v.finReal()).toMinutes();
                if ("FINALIZADA".equals(v.estado()) || "CANCELADA".equals(v.estado())) {
                    minutosReales += minutosVisita;
                    if (v.monto() != null) total = total.add(v.monto());
                }
                if ("FINALIZADA".equals(v.estado())) {
                    finalizadas++;
                    minutosFinalizados += minutosVisita;
                }
                if ("ACTIVA".equals(v.estado())) activas++;
                String duracion = v.finReal() == null ? "—" :
                        formatoDuracion(minutosVisita);
                modelo.addRow(new Object[]{v.id(), v.cliente(), v.dni(), v.estado(),
                        v.equipo(), v.tipo(), duracion, formato(v.inicio()),
                        formato(v.finProgramada()), formato(v.finReal()), v.precioHora(),
                        v.monto() == null ? "—" : v.monto(),
                        v.ticket() == null ? "—" : v.ticket(), v.empleado()});
            }
            resumen.setText("Visitas: " + visitas.size() + "  ·  Finalizadas: "
                    + finalizadas + "  ·  Activas: " + activas
                    + "  ·  Uso finalizado: " + formatoDuracion(minutosFinalizados)
                    + "  ·  Uso real total: " + formatoDuracion(minutosReales)
                    + "  ·  Cobrado: S/ " + total);
            actualizarGraficos();
            graficos.setVisible(!visitas.isEmpty());
            pie.setPreferredSize(new Dimension(0, visitas.isEmpty() ? 75 : 210));
            actualizarAcciones();
            revalidate();
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Usa fechas con formato AAAA-MM-DD.",
                    "Fecha inválida", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudo cargar el historial", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formato(LocalDateTime fecha) {
        return fecha == null ? "—" : fecha.format(FECHA_HORA);
    }

    private String formatoDuracion(long minutos) {
        if (minutos < 60) return minutos + " min";
        long horas = minutos / 60;
        long resto = minutos % 60;
        return resto == 0 ? horas + " h" : horas + " h " + resto + " min";
    }

    private void actualizarGraficos() {
        graficos.removeAll();
        JPanel porEquipo = bloque("Visitas por tipo (incluye canceladas)");
        JPanel porEstado = bloque("Visitas por estado");
        for (String t : new String[]{"PC Gamer", "PC Normal", "Consola"}) {
            long cantidad = visitas.stream().filter(v -> t.equals(v.tipo())).count();
            long minutos = visitas.stream()
                    .filter(v -> t.equals(v.tipo()) && v.finReal() != null)
                    .mapToLong(v -> Duration.between(v.inicio(), v.finReal()).toMinutes())
                    .sum();
            JProgressBar barra = barra(porEquipo, t, cantidad, visitas.size());
            barra.setToolTipText("Uso real registrado: " + formatoDuracion(minutos)
                    + ". Las canceladas se cobran por el tiempo contratado.");
        }
        for (String e : new String[]{"ACTIVA", "FINALIZADA", "CANCELADA"}) {
            long cantidad = visitas.stream().filter(v -> e.equals(v.estado())).count();
            barra(porEstado, e, cantidad, visitas.size());
        }
        graficos.add(porEquipo);
        graficos.add(porEstado);
        graficos.revalidate();
        graficos.repaint();
    }

    private JPanel bloque(String titulo) {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 3));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(6, 10, 6, 10));
        panel.add(new JLabel(titulo));
        return panel;
    }

    private JProgressBar barra(JPanel panel, String nombre, long cantidad, long base) {
        int porcentaje = base == 0 ? 0 : (int) Math.round(100.0 * cantidad / base);
        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue(porcentaje);
        barra.setStringPainted(true);
        barra.setString(nombre + ": " + porcentaje + "% · " + cantidad
                + (cantidad == 1 ? " visita" : " visitas"));
        panel.add(barra);
        return barra;
    }

    private void terminar(boolean cancelar) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una sesión de la tabla.");
            return;
        }
        SesionDAO.Visita visita = visitas.get(fila);
        if (!"ACTIVA".equals(visita.estado())) {
            JOptionPane.showMessageDialog(this, "La sesión seleccionada ya terminó.");
            return;
        }
        String accion = cancelar ? "cancelar" : "finalizar";
        String detalleCobro = cancelar
                ? "\nSe cobrará el tiempo contratado completo, hasta "
                        + formato(visita.finProgramada()) + "."
                : "";
        if (JOptionPane.showConfirmDialog(this, "¿Deseas " + accion + " la sesión "
                + visita.id() + "?" + detalleCobro, "Confirmar", JOptionPane.YES_NO_OPTION)
                != JOptionPane.YES_OPTION) return;
        try {
            dao.terminar(usuario.rol(), visita.id(), cancelar);
            cargar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudo terminar la sesión", JOptionPane.ERROR_MESSAGE);
        }
    }
}
