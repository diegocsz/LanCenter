package com.mycompany.lancenter;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PanelTickets extends JPanel {

    private JTable tablaTickets;

    public PanelTickets() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        JLabel titulo = new JLabel("TICKETS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(titulo, BorderLayout.NORTH);

        String[] columnas = {
            "Ticket", "Nombre", "Hora Inicio", "Hora Fin",
            "Equipo", "Tipo", "MONTO"
        };

        Object[][] datos = {
            {"T0001", "Diego Salcedo", "10:15 AM", "11:45 AM", "PC4", "G", "S/4.50"},
            {"T0002", "Mathias Medrano", "10:15 AM", "11:45 AM", "PC5", "G", "S/4.50"},
            {"T0003", "Patrick Quispe", "12:00 PM", "3:00 PM", "PC7", "E", "S/6.00"}
        };

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaTickets = new JTable(modelo);
        tablaTickets.setRowHeight(40);
        tablaTickets.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaTickets.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaTickets.getTableHeader().setBackground(new Color(220, 224, 230));
        tablaTickets.getTableHeader().setForeground(Color.BLACK);
        tablaTickets.setGridColor(new Color(230, 230, 230));
        tablaTickets.setSelectionBackground(new Color(210, 230, 250));
        tablaTickets.setSelectionForeground(Color.BLACK);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tablaTickets.getColumnCount(); i++) {
            tablaTickets.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(centrado);
        }

        JScrollPane scroll = new JScrollPane(tablaTickets);
        scroll.setBorder(new LineBorder(new Color(210, 210, 210)));

        JPanel contenedorTabla = new JPanel(new BorderLayout());
        contenedorTabla.setBackground(new Color(240, 240, 240));
        contenedorTabla.setBorder(new EmptyBorder(0, 20, 20, 20));
        contenedorTabla.add(scroll, BorderLayout.CENTER);

        add(contenedorTabla, BorderLayout.CENTER);
    }
}