package com.mycompany.lancenter;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class NewJFrame extends JFrame {
    
    public NewJFrame() {
        initComponents();
    }

    private void initComponents() {

        setTitle("Sistema de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ====== BARRA LATERAL ======
        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(180, 700));
        menu.setBackground(new Color(33, 43, 54));
        menu.setLayout(new GridLayout(3, 1, 0, 10));
        menu.setBorder(new EmptyBorder(30, 10, 30, 10));

        JButton btnUsuarios = crearBotonMenu("Registro Usuarios", false);
        JButton btnEquipos = crearBotonMenu("Equipos", true);
        JButton btnTickets = crearBotonMenu("Tickets", false);

        menu.add(btnUsuarios);
        menu.add(btnEquipos);
        menu.add(btnTickets);

        btnUsuarios.addActionListener(e -> {
    getContentPane().removeAll();

    add(menu, BorderLayout.WEST);
    add(new PanelRegistroUsuarios(), BorderLayout.CENTER);

    revalidate();
    repaint();
});
        
        add(menu, BorderLayout.WEST);

        // ====== PANEL PRINCIPAL ======
        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(new Color(240, 240, 240));

        JLabel titulo = new JLabel("EQUIPOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setBorder(new EmptyBorder(20, 20, 10, 0));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(new Color(240, 240, 240));

        // COMPUTADORAS
        contenido.add(crearTitulo("Computadoras (12)"));

        JPanel pcs = new JPanel(new GridLayout(3, 4, 15, 15));
        pcs.setOpaque(false);
        pcs.setBorder(new EmptyBorder(10, 20, 20, 20));

        for (int i = 1; i <= 12; i++) {
            String nombre = String.format("PC%02d", i);
            Color color = (i <= 6)
                    ? new Color(220, 70, 70)
                    : new Color(60, 180, 75);

            pcs.add(crearEquipo(nombre, color));
        }

        contenido.add(pcs);

        // CONSOLAS
        contenido.add(crearTitulo("Consolas (5)"));

        JPanel consolas = new JPanel(new GridLayout(1, 5, 15, 15));
        consolas.setOpaque(false);
        consolas.setBorder(new EmptyBorder(10, 20, 20, 20));

        String[] nombres = {"PS1", "PS2", "XBOX1", "SWITCH1", "SWITCH2"};

        for (int i = 0; i < nombres.length; i++) {
            Color color = (i < 2)
                    ? new Color(220, 70, 70)
                    : new Color(60, 180, 75);

            consolas.add(crearEquipo(nombres[i], color));
        }

        contenido.add(consolas);

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        principal.add(scroll, BorderLayout.CENTER);

        add(principal, BorderLayout.CENTER);
    }

    // ===== BOTÓN DEL MENÚ =====
    private JButton crearBotonMenu(String texto, boolean activo) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setBorder(new EmptyBorder(12, 10, 12, 10));

        if (activo)
            b.setBackground(new Color(33, 150, 243));
        else
            b.setBackground(new Color(55, 71, 79));

       

        return b;
    }

    // ===== TÍTULO DE SECCIÓN =====
    private JLabel crearTitulo(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l.setBorder(new EmptyBorder(10, 20, 0, 0));
        return l;
    }

    // ===== TARJETA DE EQUIPO =====
    private JPanel crearEquipo(String nombre, Color color) {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setPreferredSize(new Dimension(150, 90));
        p.setBorder(new CompoundBorder(
                new LineBorder(Color.DARK_GRAY, 1),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel icono = new JLabel("🖥");
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        icono.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel texto = new JLabel(nombre);
        texto.setFont(new Font("Segoe UI", Font.BOLD, 18));
        texto.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(icono, BorderLayout.CENTER);
        p.add(texto, BorderLayout.SOUTH);

        p.setCursor(new Cursor(Cursor.HAND_CURSOR));

        p.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(
                        NewJFrame.this,
                        "XD",
                        nombre,
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        return p;
    }
}
