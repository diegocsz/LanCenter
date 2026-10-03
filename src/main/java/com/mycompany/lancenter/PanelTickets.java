package com.mycompany.lancenter;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;

public class PanelTickets extends JPanel {

    private static final Color FONDO = new Color(240, 240, 240);
    private static final Color AZUL = new Color(33, 150, 243);
    private static final Color VERDE = new Color(60, 180, 75);

    // codigo, nombre, hora inicio, hora fin, monto
    private final Object[][] tickets = {
        {"T0001", "Diego Salcedo", "10:15 AM", "11:45 AM", 4.50},
        {"T0002", "Mathias Medrano", "10:15 AM", "11:45 AM", 4.50},
        {"T0003", "Patrick Quispe", "12:00 PM", "3:00 PM", 6.00},
        {"T0004", "Ana Torres", "12:30 PM", "1:30 PM", 3.00},
        {"T0005", "Luis Ramos", "1:00 PM", "2:30 PM", 4.50},
        {"T0006", "Carlos Vega", "2:00 PM", "4:00 PM", 4.00}
    };

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

        cuerpo.add(crearListaTickets(), BorderLayout.CENTER);
        cuerpo.add(crearMetricas(), BorderLayout.EAST);

        add(cuerpo, BorderLayout.CENTER);
    }

    // =====================================================
    // IZQUIERDA: LISTA DE TICKETS
    // =====================================================
    private JScrollPane crearListaTickets() {
        JPanel grid = new JPanel(new GridLayout(0, 2, 15, 15));
        grid.setOpaque(false);

        for (Object[] t : tickets) {
            grid.add(crearTicket(
                    (String) t[0],
                    (String) t[1],
                    (String) t[2],
                    (String) t[3],
                    (double) t[4]));
        }

        // Para que los tickets no se estiren hacia abajo
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO);
        contenedor.add(grid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(contenedor);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel crearTicket(String codigo, String nombre,
                               String inicio, String fin, double monto) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(250, 210));
        p.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 210, 210), 1),
                new EmptyBorder(15, 18, 15, 18)));

        JLabel local = new JLabel("LAN CENTER");
        local.setFont(new Font("Segoe UI", Font.BOLD, 18));
        local.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCodigo = new JLabel("Ticket " + codigo);
        lblCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCodigo.setForeground(Color.GRAY);
        lblCodigo.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(local);
        p.add(lblCodigo);
        p.add(Box.createVerticalStrut(6));
        p.add(new LineaPunteada());
        p.add(Box.createVerticalStrut(6));

        p.add(crearFila("Cliente", nombre, 13, false, Color.BLACK));
        p.add(Box.createVerticalStrut(4));
        p.add(crearFila("Inicio", inicio, 13, false, Color.BLACK));
        p.add(Box.createVerticalStrut(4));
        p.add(crearFila("Fin", fin, 13, false, Color.BLACK));

        p.add(Box.createVerticalStrut(6));
        p.add(new LineaPunteada());
        p.add(Box.createVerticalStrut(6));

        p.add(crearFila("TOTAL", formatoMonto(monto), 18, true, AZUL));

        return p;
    }

    private JPanel crearFila(String etiqueta, String valor, int tam,
                             boolean negrita, Color colorValor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        fila.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel l = new JLabel(etiqueta);
        l.setFont(new Font("Segoe UI", negrita ? Font.BOLD : Font.PLAIN, tam));
        l.setForeground(negrita ? Color.BLACK : Color.GRAY);

        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.BOLD, tam));
        v.setForeground(colorValor);

        fila.add(l, BorderLayout.WEST);
        fila.add(v, BorderLayout.EAST);
        return fila;
    }

    // =====================================================
    // DERECHA: MÉTRICAS DEL DÍA
    // =====================================================
    private JPanel crearMetricas() {
        double total = 0;
        for (Object[] t : tickets) {
            total += (double) t[4];
        }
        int personas = tickets.length;
        double promedio = (personas > 0) ? total / personas : 0;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(280, 0));

        JLabel titulo = new JLabel("Resumen del día");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFecha.setForeground(Color.GRAY);
        lblFecha.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titulo);
        panel.add(lblFecha);
        panel.add(Box.createVerticalStrut(15));
        panel.add(crearMetrica("Monto acumulado", formatoMonto(total), VERDE));
        panel.add(Box.createVerticalStrut(15));
        panel.add(crearMetrica("Sesiones alquiladas", String.valueOf(personas), AZUL));
        panel.add(Box.createVerticalStrut(15));
        panel.add(crearMetrica("Promedio por ticket", formatoMonto(promedio), AZUL));

        return panel;
    }

    private JPanel crearMetrica(String etiqueta, String valor, Color colorValor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 210, 210), 1),
                new EmptyBorder(15, 18, 15, 18)));

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

    private String formatoMonto(double monto) {
        return String.format(Locale.US, "S/ %.2f", monto);
    }

    // =====================================================
    // LÍNEA PUNTEADA (separador del ticket)
    // =====================================================
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
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10f, new float[]{5f, 4f}, 0f));
            g2.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
            g2.dispose();
        }
    }
}