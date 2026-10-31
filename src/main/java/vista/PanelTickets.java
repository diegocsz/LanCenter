package vista;

import dao.TicketDAO;
import modelo.Ticket;
import dao.MetricasTicketsDAO;
import modelo.MetricasTickets;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;

public class PanelTickets extends JPanel {

    private static final Color FONDO = new Color(240, 240, 240);
    private static final Color AZUL = new Color(33, 150, 243);
    private static final Color VERDE = new Color(60, 180, 75);
    private static final Color NARANJA = new Color(245, 145, 40);
    private static final Color MORADO = new Color(140, 100, 220);

    private static final DateTimeFormatter FORMATO_HORA
            = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);

    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JPanel contenedorTickets;
    private final JPanel panelMetricas;

    public PanelTickets() {

        setLayout(new BorderLayout());
        setBackground(FONDO);

        JLabel titulo = new JLabel("TICKETS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setBorder(new EmptyBorder(20, 20, 20, 20));

        add(titulo, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(20, 0));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(new EmptyBorder(0, 20, 20, 20));

        contenedorTickets = new JPanel(new GridLayout(0, 2, 15, 15));
        contenedorTickets.setOpaque(false);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO);
        contenedor.add(contenedorTickets, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(contenedor);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        cuerpo.add(scroll, BorderLayout.CENTER);

        panelMetricas = new JPanel();
        panelMetricas.setLayout(
                new BoxLayout(panelMetricas, BoxLayout.Y_AXIS)
        );
        panelMetricas.setOpaque(false);
        panelMetricas.setPreferredSize(new Dimension(280, 0));

        cuerpo.add(panelMetricas, BorderLayout.EAST);

        add(cuerpo, BorderLayout.CENTER);

        cargarTickets();
    }

    public final void cargarTickets() {

        try {

            TicketDAO ticketDAO = new TicketDAO();

            List<Ticket> tickets = ticketDAO.listarTickets();

            mostrarTickets(tickets);

            MetricasTicketsDAO metricasDAO = new MetricasTicketsDAO();

            MetricasTickets metricas
                    = metricasDAO.obtenerMetricasDelDia();

            mostrarMetricas(metricas);

        } catch (SQLException e) {

            contenedorTickets.removeAll();
            panelMetricas.removeAll();

            JLabel error = new JLabel(
                    "<html><div style='text-align:center;'>"
                    + "No se pudieron cargar los datos.<br>"
                    + "Verifica la conexión con MySQL."
                    + "</div></html>"
            );

            error.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            error.setHorizontalAlignment(SwingConstants.CENTER);

            contenedorTickets.setLayout(new BorderLayout());
            contenedorTickets.add(error, BorderLayout.NORTH);

            revalidate();
            repaint();

            JOptionPane.showMessageDialog(
                    this,
                    "Error al consultar los datos:\n" + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void mostrarTickets(List<Ticket> tickets) {

        contenedorTickets.removeAll();
        contenedorTickets.setLayout(new GridLayout(0, 2, 15, 15));

        if (tickets.isEmpty()) {

            JPanel vacio = new JPanel();
            vacio.setLayout(
                    new BoxLayout(vacio, BoxLayout.Y_AXIS)
            );
            vacio.setBackground(Color.WHITE);
            vacio.setBorder(
                    new CompoundBorder(
                            new LineBorder(new Color(210, 210, 210)),
                            new EmptyBorder(30, 20, 30, 20)
                    )
            );

            JLabel titulo = new JLabel("Todavía no hay tickets");
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 19));
            titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel descripcion = new JLabel(
                    "<html><div style='text-align:center;'>"
                    + "Los tickets aparecerán aquí automáticamente "
                    + "cuando se finalicen sesiones de alquiler."
                    + "</div></html>"
            );
            descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            descripcion.setForeground(Color.GRAY);
            descripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

            vacio.add(Box.createVerticalGlue());
            vacio.add(titulo);
            vacio.add(Box.createVerticalStrut(10));
            vacio.add(descripcion);
            vacio.add(Box.createVerticalGlue());

            contenedorTickets.setLayout(new BorderLayout());
            contenedorTickets.add(vacio, BorderLayout.NORTH);

        } else {

            for (Ticket ticket : tickets) {
                contenedorTickets.add(crearTicket(ticket));
            }
        }

        contenedorTickets.revalidate();
        contenedorTickets.repaint();
    }

    private JPanel crearTicket(Ticket ticket) {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);

        p.setPreferredSize(new Dimension(250, 210));

        p.setBorder(
                new CompoundBorder(
                        new LineBorder(new Color(210, 210, 210), 1),
                        new EmptyBorder(15, 18, 15, 18)
                )
        );

        JLabel local = new JLabel("LAN CENTER");
        local.setFont(new Font("Segoe UI", Font.BOLD, 18));
        local.setAlignmentX(Component.CENTER_ALIGNMENT);

        String codigo = String.format(
                Locale.ROOT,
                "T%04d",
                ticket.getIdTicket()
        );

        JLabel lblCodigo = new JLabel("Ticket " + codigo);
        lblCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCodigo.setForeground(Color.GRAY);
        lblCodigo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblFecha = new JLabel(
                ticket.getFechaTicket().format(FORMATO_FECHA)
        );
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFecha.setForeground(Color.GRAY);
        lblFecha.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(local);
        p.add(lblCodigo);
        p.add(lblFecha);
        p.add(Box.createVerticalStrut(6));
        p.add(new LineaPunteada());
        p.add(Box.createVerticalStrut(6));

        p.add(
                crearFila(
                        "Cliente",
                        ticket.getNombreCliente(),
                        13,
                        false,
                        Color.BLACK
                )
        );

        p.add(Box.createVerticalStrut(4));

        p.add(
                crearFila(
                        "Inicio",
                        ticket.getHoraInicio().format(FORMATO_HORA),
                        13,
                        false,
                        Color.BLACK
                )
        );

        p.add(Box.createVerticalStrut(4));

        p.add(
                crearFila(
                        "Fin",
                        ticket.getHoraFin().format(FORMATO_HORA),
                        13,
                        false,
                        Color.BLACK
                )
        );

        p.add(Box.createVerticalStrut(6));
        p.add(new LineaPunteada());
        p.add(Box.createVerticalStrut(6));

        p.add(
                crearFila(
                        "TOTAL",
                        formatoMonto(ticket.getMonto()),
                        18,
                        true,
                        AZUL
                )
        );

        return p;
    }

    private JPanel crearFila(
            String etiqueta,
            String valor,
            int tam,
            boolean negrita,
            Color colorValor) {

        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        fila.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel l = new JLabel(etiqueta);
        l.setFont(
                new Font(
                        "Segoe UI",
                        negrita ? Font.BOLD : Font.PLAIN,
                        tam
                )
        );
        l.setForeground(negrita ? Color.BLACK : Color.GRAY);

        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.BOLD, tam));
        v.setForeground(colorValor);

        fila.add(l, BorderLayout.WEST);
        fila.add(v, BorderLayout.EAST);

        return fila;
    }

    private void mostrarMetricas(MetricasTickets metricas) {

        panelMetricas.removeAll();

        LocalDate hoy = LocalDate.now();

        JLabel titulo = new JLabel("Resumen del día");

        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fecha = new JLabel(hoy.format(FORMATO_FECHA));

        fecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fecha.setForeground(Color.GRAY);
        fecha.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelMetricas.add(titulo);
        panelMetricas.add(fecha);
        panelMetricas.add(Box.createVerticalStrut(15));

        panelMetricas.add(
                crearMetrica(
                        "Monto acumulado",
                        formatoMonto(metricas.getMontoAcumulado()),
                        VERDE
                )
        );

        panelMetricas.add(Box.createVerticalStrut(12));

        panelMetricas.add(
                crearMetrica(
                        "Sesiones alquiladas",
                        String.valueOf(metricas.getSesionesAlquiladas()),
                        AZUL
                )
        );

        panelMetricas.add(Box.createVerticalStrut(12));

        panelMetricas.add(
                crearMetricaEquipos(metricas)
        );

        panelMetricas.add(Box.createVerticalStrut(12));

        panelMetricas.add(
                crearMetrica(
                        "Horas alquiladas",
                        formatoDuracion(metricas.getMinutosAlquilados()),
                        MORADO
                )
        );

        panelMetricas.add(Box.createVerticalStrut(12));

        panelMetricas.add(
                crearMetrica(
                        "Promedio por ticket",
                        formatoMonto(metricas.getPromedioTicket()),
                        AZUL
                )
        );

        panelMetricas.add(Box.createVerticalStrut(12));

        panelMetricas.add(
                crearMetrica(
                        "Duración promedio",
                        formatoDuracion(
                                metricas.getDuracionPromedioMinutos()
                        ),
                        MORADO
                )
        );

        panelMetricas.add(Box.createVerticalStrut(12));

        panelMetricas.add(
                crearMetrica(
                        "Ticket de mayor monto",
                        formatoMonto(metricas.getTicketMayorMonto()),
                        NARANJA
                )
        );

        panelMetricas.revalidate();
        panelMetricas.repaint();
    }

    private JPanel crearMetrica(
            String etiqueta,
            String valor,
            Color colorValor) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        card.setBorder(
                new CompoundBorder(
                        new LineBorder(new Color(210, 210, 210), 1),
                        new EmptyBorder(15, 18, 15, 18)
                )
        );

        JLabel l = new JLabel(etiqueta);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(Color.GRAY);

        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.BOLD, 30));
        v.setForeground(colorValor);

        card.add(l);
        card.add(Box.createVerticalStrut(4));
        card.add(v);

        return card;
    }

    private JPanel crearMetricaEquipos(MetricasTickets metricas) {

        JPanel card = new JPanel();

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

        card.setBorder(
                new CompoundBorder(
                        new LineBorder(new Color(210, 210, 210), 1),
                        new EmptyBorder(12, 16, 12, 16)
                )
        );

        JLabel titulo = new JLabel("Equipos utilizados");

        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titulo.setForeground(Color.GRAY);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel normal = new JLabel(
                "NORMAL: " + metricas.getEquiposNormales()
        );

        normal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        normal.setForeground(AZUL);
        normal.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel gamer = new JLabel(
                "GAMER: " + metricas.getEquiposGamer()
        );

        gamer.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gamer.setForeground(VERDE);
        gamer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel consolas = new JLabel(
                "CONSOLAS: " + metricas.getConsolas()
        );

        consolas.setFont(new Font("Segoe UI", Font.BOLD, 18));
        consolas.setForeground(MORADO);
        consolas.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titulo);
        card.add(Box.createVerticalStrut(8));
        card.add(normal);
        card.add(Box.createVerticalStrut(3));
        card.add(gamer);
        card.add(Box.createVerticalStrut(3));
        card.add(consolas);

        return card;
    }

    private String formatoDuracion(long minutos) {

        long horas = minutos / 60;
        long minutosRestantes = minutos % 60;

        if (horas > 0 && minutosRestantes > 0) {
            return horas + " h " + minutosRestantes + " min";
        }

        if (horas > 0) {
            return horas + " h";
        }

        return minutosRestantes + " min";
    }
    
    private String formatoMonto(BigDecimal monto) {
        return String.format(
                Locale.US,
                "S/ %.2f",
                monto
        );
    }

    private static class LineaPunteada extends JComponent {

        LineaPunteada() {
            setPreferredSize(new Dimension(10, 10));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(new Color(170, 170, 170));

            g2.setStroke(
                    new BasicStroke(
                            1f,
                            BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER,
                            10f,
                            new float[]{5f, 4f},
                            0f
                    )
            );

            g2.drawLine(
                    0,
                    getHeight() / 2,
                    getWidth(),
                    getHeight() / 2
            );

            g2.dispose();
        }
    }
}
