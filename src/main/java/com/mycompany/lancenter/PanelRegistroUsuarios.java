package com.mycompany.lancenter;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class PanelRegistroUsuarios extends JPanel {

    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JButton btnRegistrar;

    public PanelRegistroUsuarios() {

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // ===== TÍTULO =====
        JLabel titulo = new JLabel("REGISTRO DE USUARIOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(titulo, BorderLayout.NORTH);

        // ===== CONTENIDO =====
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(new Color(240, 240, 240));
        contenido.setBorder(new EmptyBorder(10, 30, 30, 30));

        // ===== DATOS DEL CLIENTE =====
        JPanel panelCliente = new JPanel(new GridLayout(5, 2, 15, 15));
        panelCliente.setBackground(Color.WHITE);
        panelCliente.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(180, 180, 180)), "Datos del Cliente"),
                new EmptyBorder(20, 20, 20, 20)));

        txtDni = new JTextField();
        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();

        panelCliente.add(crearLabel("DNI:"));
        panelCliente.add(txtDni);
        panelCliente.add(crearLabel("Nombre:"));
        panelCliente.add(txtNombre);
        panelCliente.add(crearLabel("Apellido:"));
        panelCliente.add(txtApellido);
        panelCliente.add(crearLabel("Teléfono:"));
        panelCliente.add(txtTelefono);
        panelCliente.add(crearLabel("Correo:"));
        panelCliente.add(txtCorreo);

        contenido.add(panelCliente);

        // ===== BOTÓN REGISTRAR =====
        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setBackground(new Color(33, 150, 243));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setPreferredSize(new Dimension(200, 50));

        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);
        panelBoton.setBorder(new EmptyBorder(20, 0, 0, 0));
        panelBoton.add(btnRegistrar);

        contenido.add(panelBoton);

        btnRegistrar.addActionListener(e -> registrar());

        add(contenido, BorderLayout.CENTER);
    }

    private void registrar() {
        if (txtDni.getText().isEmpty()
                || txtNombre.getText().isEmpty()
                || txtApellido.getText().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe completar DNI, Nombre y Apellido.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Cliente registrado correctamente",
                "Registro",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }
}