package com.mycompany.lancenter;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtClave;

    public LoginFrame() {

        setTitle("Sistema de Gestión - Login");
        setSize(450, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(new Color(240,240,240));

        // Cabecera
        JPanel header = new JPanel();
        header.setBackground(new Color(33,43,54));
        header.setPreferredSize(new Dimension(450,80));

        JLabel titulo = new JLabel("LAN CENTER");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(titulo);

        // Formulario
        JPanel form = new JPanel();
        form.setBackground(new Color(240,240,240));
        form.setBorder(new EmptyBorder(25,35,25,35));
        form.setLayout(new GridLayout(5,1,0,10));

        JLabel lblCorreo = new JLabel("Correo");
        lblCorreo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtCorreo = new JTextField();

        JLabel lblClave = new JLabel("Contraseña");
        lblClave.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtClave = new JPasswordField();

        JButton btnLogin = new JButton("Iniciar sesión");
        btnLogin.setBackground(new Color(33,150,243));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnLogin.addActionListener(e -> login());

        form.add(lblCorreo);
        form.add(txtCorreo);
        form.add(lblClave);
        form.add(txtClave);
        form.add(btnLogin);

        fondo.add(header, BorderLayout.NORTH);
        fondo.add(form, BorderLayout.CENTER);

        add(fondo);
    }

    private void login() {

        String correo = txtCorreo.getText();
        String clave = String.valueOf(txtClave.getPassword());

        if (correo.equals("admin@lancenter.com") &&
            clave.equals("123456")) {

            new NewJFrame().setVisible(true);
            dispose();

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Correo o contraseña incorrectos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}