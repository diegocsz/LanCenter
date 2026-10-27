package com.mycompany.lancenter;

import dao.SesionDAO;
import modelo.Equipo;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DialogSesionAlquiler extends JDialog {
    private final Equipo equipo;
    private final int idEmpleado;
    private final Runnable alGuardar;
    private final JComboBox<SesionDAO.Cliente> clientes = new JComboBox<>();
    private final JComboBox<Integer> minutos = new JComboBox<>();
    private final JLabel fin = new JLabel();
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DialogSesionAlquiler(Frame padre, Equipo equipo, int idEmpleado, Runnable alGuardar) {
        super(padre, "Sesión de alquiler", true);
        this.equipo = equipo;
        this.idEmpleado = idEmpleado;
        this.alGuardar = alGuardar;
        setLayout(new BorderLayout(12, 12));
        JPanel campos = new JPanel(new GridLayout(5, 2, 12, 12));
        campos.setBorder(new EmptyBorder(22, 22, 12, 22));
        campos.add(new JLabel("Cliente:"));
        campos.add(clientes);
        campos.add(new JLabel("Equipo:"));
        campos.add(new JLabel(equipo.getNombre() + " · " + equipo.getTipo()));
        campos.add(new JLabel("Inicio:"));
        campos.add(new JLabel(LocalDateTime.now().format(FORMATO)));
        campos.add(new JLabel("Duración reservada:"));
        for (int m = 30; m <= 720; m += 30) minutos.addItem(m);
        campos.add(minutos);
        campos.add(new JLabel("Fin programado:"));
        campos.add(fin);
        minutos.addActionListener(e -> actualizarFin());
        actualizarFin();
        add(campos, BorderLayout.CENTER);

        JButton iniciar = new JButton("Empezar sesión");
        iniciar.addActionListener(e -> iniciar());
        JPanel pie = new JPanel();
        pie.setBorder(new EmptyBorder(0, 0, 15, 0));
        pie.add(iniciar);
        add(pie, BorderLayout.SOUTH);
        setSize(510, 350);
        setLocationRelativeTo(padre);
        try {
            for (SesionDAO.Cliente cliente : new SesionDAO().listarClientes()) clientes.addItem(cliente);
            if (clientes.getItemCount() == 0) iniciar.setEnabled(false);
        } catch (SQLException ex) {
            iniciar.setEnabled(false);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de MySQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarFin() {
        Integer duracion = (Integer) minutos.getSelectedItem();
        if (duracion != null) fin.setText(LocalDateTime.now().plusMinutes(duracion).format(FORMATO));
    }

    private void iniciar() {
        SesionDAO.Cliente cliente = (SesionDAO.Cliente) clientes.getSelectedItem();
        Integer duracion = (Integer) minutos.getSelectedItem();
        if (cliente == null || duracion == null) return;
        try {
            new SesionDAO().iniciar(cliente.id(), idEmpleado, equipo,
                    LocalDateTime.now().plusMinutes(duracion));
            JOptionPane.showMessageDialog(this, "Sesión iniciada correctamente.");
            dispose();
            alGuardar.run();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo iniciar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
