package com.mycompany.lancenter;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.*;

public class DialogSesionAlquiler extends JDialog {

    private JComboBox<String> cbUsuario;
    private JComboBox<String> cbTipoEquipo;
    private JComboBox<String> cbEquipo;
    private JComboBox<String> cbHoraFin;
    private JLabel lblHoraInicio;
    private JButton btnIniciarSesion;

    public DialogSesionAlquiler(Frame padre, String nombreEquipo) {
        super(padre, "Sesión de Alquiler", true);

        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        // ===== PANEL SESIÓN =====
        JPanel panelSesion = new JPanel(new GridLayout(5, 2, 15, 15));
        panelSesion.setBackground(Color.WHITE);
        panelSesion.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(180, 180, 180)), "Sesión de Alquiler"),
                new EmptyBorder(20, 20, 20, 20)));

        // Usuario (seleccionable)
        cbUsuario = new JComboBox<>();
        cbUsuario.addItem("Seleccionar usuario");
        cbUsuario.addItem("Diego Salcedo");
        cbUsuario.addItem("Usuario 2");
        cbUsuario.setSelectedItem("Diego Salcedo");

        // Tipo de equipo (fijo: una sola opción, no se puede cambiar)
        cbTipoEquipo = new JComboBox<>();
        cbTipoEquipo.addItem("PC Gamer");

        // Equipo (fijo: una sola opción, no se puede cambiar)
        cbEquipo = new JComboBox<>();
        cbEquipo.addItem("PC07-GAMER");

        // Hora de inicio (ahora)
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblHoraInicio = new JLabel(ahora.format(formato));
        lblHoraInicio.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        // Hora de fin (selector cada 30 min, hasta 12 horas)
        cbHoraFin = new JComboBox<>();
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm a");

        LocalDateTime opcion = ahora.truncatedTo(ChronoUnit.HOURS);
        while (opcion.isBefore(ahora)) {
            opcion = opcion.plusMinutes(30);
        }
        if (ChronoUnit.MINUTES.between(ahora, opcion) < 30) {
            opcion = opcion.plusMinutes(30);
        }

        for (int i = 0; i < 24; i++) {
            String texto = opcion.format(formatoHora);
            if (opcion.toLocalDate().isAfter(ahora.toLocalDate())) {
                texto += " (+1 día)";
            }
            cbHoraFin.addItem(texto);
            opcion = opcion.plusMinutes(30);
        }

        panelSesion.add(crearLabel("Usuario:"));
        panelSesion.add(cbUsuario);
        panelSesion.add(crearLabel("Tipo de equipo:"));
        panelSesion.add(cbTipoEquipo);
        panelSesion.add(crearLabel("Equipo:"));
        panelSesion.add(cbEquipo);
        panelSesion.add(crearLabel("Hora de inicio:"));
        panelSesion.add(lblHoraInicio);
        panelSesion.add(crearLabel("Hora de fin:"));
        panelSesion.add(cbHoraFin);

        // ===== BOTÓN =====
        btnIniciarSesion = new JButton("EMPEZAR SESIÓN");
        btnIniciarSesion.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnIniciarSesion.setForeground(Color.WHITE);
        btnIniciarSesion.setBackground(new Color(33, 150, 243));
        btnIniciarSesion.setFocusPainted(false);
        btnIniciarSesion.setPreferredSize(new Dimension(200, 50));

        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);
        panelBoton.setBorder(new EmptyBorder(10, 0, 15, 0));
        panelBoton.add(btnIniciarSesion);

        btnIniciarSesion.addActionListener(e -> iniciarSesion());

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setOpaque(false);
        contenido.setBorder(new EmptyBorder(15, 20, 0, 20));
        contenido.add(panelSesion, BorderLayout.CENTER);
        contenido.add(panelBoton, BorderLayout.SOUTH);

        add(contenido, BorderLayout.CENTER);

        setPreferredSize(new Dimension(480, 430));
        pack();
        setLocationRelativeTo(padre);
    }

    private void iniciarSesion() {
        if (cbUsuario.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un usuario.",
                    "Usuario",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mensaje = "Sesión iniciada correctamente\n\n"
                + "Usuario: " + cbUsuario.getSelectedItem() + "\n"
                + "Tipo de equipo: " + cbTipoEquipo.getSelectedItem() + "\n"
                + "Equipo: " + cbEquipo.getSelectedItem() + "\n"
                + "Hora inicio: " + lblHoraInicio.getText() + "\n"
                + "Hora fin: " + cbHoraFin.getSelectedItem();

        JOptionPane.showMessageDialog(this, mensaje, "Sesión iniciada",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }
}