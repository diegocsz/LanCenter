package com.mycompany.lancenter;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.*;

public class DialogSesionAlquiler extends JDialog {

    private JComboBox<String> cbUsuario;
    private JComboBox<String> cbTipoEquipo;
    private JComboBox<String> cbEquipo;
    private JLabel lblMediasHoras;
    private JLabel lblHoraInicio;
    private JButton btnIniciarSesion;

    private int mediasHoras = 1;

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

        // Medias horas con botones - y +
        lblMediasHoras = new JLabel(String.valueOf(mediasHoras), SwingConstants.CENTER);
        lblMediasHoras.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMediasHoras.setPreferredSize(new Dimension(50, 32));

        JButton btnMenos = crearBotonContador("-");
        JButton btnMas = crearBotonContador("+");

        btnMenos.addActionListener(e -> {
            if (mediasHoras > 1) {
                mediasHoras--;
                lblMediasHoras.setText(String.valueOf(mediasHoras));
            }
        });

        btnMas.addActionListener(e -> {
            mediasHoras++;
            lblMediasHoras.setText(String.valueOf(mediasHoras));
        });

        JPanel panelContador = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelContador.setOpaque(false);
        panelContador.add(btnMenos);
        panelContador.add(lblMediasHoras);
        panelContador.add(btnMas);

        // Hora de inicio
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblHoraInicio = new JLabel(LocalDateTime.now().format(formato));
        lblHoraInicio.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        panelSesion.add(crearLabel("Usuario:"));
        panelSesion.add(cbUsuario);
        panelSesion.add(crearLabel("Tipo de equipo:"));
        panelSesion.add(cbTipoEquipo);
        panelSesion.add(crearLabel("Equipo:"));
        panelSesion.add(cbEquipo);
        panelSesion.add(crearLabel("Medias horas:"));
        panelSesion.add(panelContador);
        panelSesion.add(crearLabel("Hora de inicio:"));
        panelSesion.add(lblHoraInicio);

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
                + "Medias horas: " + mediasHoras + "\n"
                + "Hora inicio: " + lblHoraInicio.getText();

        JOptionPane.showMessageDialog(this, mensaje, "Sesión iniciada",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private JButton crearBotonContador(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 18));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(33, 150, 243));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(45, 32));
        return b;
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }
}