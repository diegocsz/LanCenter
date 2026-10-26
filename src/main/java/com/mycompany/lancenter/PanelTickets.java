package com.mycompany.lancenter;

import dao.SesionDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class PanelTickets extends JPanel {
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PanelTickets() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(240, 240, 240));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel titulo = new JLabel("TICKETS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        add(titulo, BorderLayout.NORTH);

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"Ticket", "Cliente", "Equipo", "Inicio", "Fin", "Total"}, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(28);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        JLabel resumen = new JLabel();
        resumen.setFont(new Font("Segoe UI", Font.BOLD, 17));
        add(resumen, BorderLayout.SOUTH);
        try {
            List<SesionDAO.TicketInfo> tickets = new SesionDAO().listarTickets();
            BigDecimal montoHoy = BigDecimal.ZERO;
            int cantidadHoy = 0;
            for (SesionDAO.TicketInfo t : tickets) {
                modelo.addRow(new Object[]{"T" + String.format("%04d", t.id()),
                        t.cliente(), t.equipo(), t.inicio().format(FORMATO),
                        t.fin().format(FORMATO), "S/ " + t.monto()});
                if (t.fin().toLocalDate().equals(LocalDate.now())) {
                    montoHoy = montoHoy.add(t.monto());
                    cantidadHoy++;
                }
            }
            resumen.setText("Hoy: " + cantidadHoy + " tickets  ·  Total: S/ " + montoHoy);
        } catch (SQLException e) {
            resumen.setText("No se pudieron cargar los tickets.");
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de MySQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
