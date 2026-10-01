package com.mycompany.lancenter;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class NewJFrame extends JFrame {

    private final Color COLOR_ACTIVO = new Color(33, 150, 243);
    private final Color COLOR_NORMAL = new Color(55, 71, 79);

    public NewJFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(180, 700));
        menu.setBackground(new Color(33, 43, 54));
        menu.setLayout(new GridLayout(3, 1, 0, 10));
        menu.setBorder(new EmptyBorder(30, 10, 30, 10));

        JButton btnUsuarios = crearBotonMenu("Registro Usuarios");
        JButton btnEquipos = crearBotonMenu("Equipos");
        JButton btnTickets = crearBotonMenu("Tickets");

        menu.add(btnUsuarios);
        menu.add(btnEquipos);
        menu.add(btnTickets);

        btnUsuarios.addActionListener(e -> {
            mostrarPanel(
                    menu,
                    new PanelRegistroUsuarios(),
                    btnUsuarios,
                    btnEquipos,
                    btnTickets
            );
        });

        btnEquipos.addActionListener(e -> {
            mostrarPanel(
                    menu,
                    crearPanelEquipos(),
                    btnEquipos,
                    btnUsuarios,
                    btnTickets
            );
        });

        btnTickets.addActionListener(e -> {
            mostrarPanel(
                    menu,
                    new PanelTickets(),
                    btnTickets,
                    btnUsuarios,
                    btnEquipos
            );
        });

        add(menu, BorderLayout.WEST);
        add(crearPanelEquipos(), BorderLayout.CENTER);

        seleccionarBoton(
                btnEquipos,
                btnUsuarios,
                btnTickets
        );
    }

    private void mostrarPanel(
            JPanel menu,
            JPanel panel,
            JButton activo,
            JButton boton2,
            JButton boton3) {

        getContentPane().removeAll();

        add(menu, BorderLayout.WEST);
        add(panel, BorderLayout.CENTER);

        seleccionarBoton(
                activo,
                boton2,
                boton3
        );

        revalidate();
        repaint();
    }

    private void seleccionarBoton(
            JButton activo,
            JButton boton2,
            JButton boton3) {

        activo.setBackground(COLOR_ACTIVO);
        boton2.setBackground(COLOR_NORMAL);
        boton3.setBackground(COLOR_NORMAL);
    }

    private JButton crearBotonMenu(String texto) {
        JButton b = new JButton(texto);

        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setBackground(COLOR_NORMAL);
        b.setBorder(new EmptyBorder(12, 10, 12, 10));

        return b;
    }

    private JPanel crearPanelEquipos() {
        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(new Color(240, 240, 240));

        JLabel titulo = new JLabel("EQUIPOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setBorder(new EmptyBorder(20, 20, 10, 0));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(new Color(240, 240, 240));

        contenido.add(crearTitulo("Computadoras (12)"));

        JPanel pcs = new JPanel(new GridLayout(3, 4, 15, 15));
        pcs.setOpaque(false);
        pcs.setBorder(new EmptyBorder(10, 20, 20, 20));

        for (int i = 1; i <= 12; i++) {
            String tipo = (i <= 8) ? "GAMER" : "NORMAL";
            String nombre = String.format("PC%02d-%s", i, tipo);

            boolean ocupado = i <= 6;

            Color color = ocupado
                    ? new Color(220, 70, 70)
                    : new Color(60, 180, 75);

            int minutos = 0;

            if (ocupado) {
                minutos = (i <= 3) ? aleatorio(1, 10) : 0;
            }

            pcs.add(crearEquipo(
                    nombre,
                    color,
                    "🖥",
                    minutos
            ));
        }

        contenido.add(pcs);

        contenido.add(crearTitulo("Consolas (5)"));

        JPanel consolas = new JPanel(new GridLayout(1, 5, 15, 15));
        consolas.setOpaque(false);
        consolas.setBorder(new EmptyBorder(10, 20, 20, 20));

        String[] nombres = {
            "PS1",
            "PS2",
            "XBOX1",
            "SWITCH1",
            "SWITCH2"
        };

        for (int i = 0; i < nombres.length; i++) {
            boolean ocupado = i < 2;

            Color color = ocupado
                    ? new Color(220, 70, 70)
                    : new Color(60, 180, 75);

            int minutos = 0;

            if (ocupado) {
                minutos = (i == 0) ? aleatorio(1, 10) : 0;
            }

            consolas.add(crearEquipo(
                    nombres[i],
                    color,
                    "🎮",
                    minutos
            ));
        }

        contenido.add(consolas);

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);

        principal.add(scroll, BorderLayout.CENTER);

        return principal;
    }

    private int aleatorio(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    private JLabel crearTitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l.setBorder(new EmptyBorder(10, 20, 0, 0));
        return l;
    }

    private JPanel crearEquipo(
            String nombre,
            Color color,
            String iconoStr,
            int minutos) {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(color);

        p.setPreferredSize(new Dimension(125, 100));
        p.setMinimumSize(new Dimension(125, 100));
        p.setMaximumSize(new Dimension(125, 100));

        p.setBorder(new CompoundBorder(
                new LineBorder(Color.DARK_GRAY, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel icono = new JLabel(iconoStr);
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel texto = new JLabel(nombre);
        texto.setFont(new Font("Segoe UI", Font.BOLD, 15));
        texto.setForeground(Color.BLACK);
        texto.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalStrut(2));
        p.add(icono);
        p.add(Box.createVerticalStrut(4));
        p.add(texto);

        if (minutos > 0) {
            JLabel lblTiempo = new JLabel(
                    "Faltan " + minutos + " min"
            );

            lblTiempo.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            12
                    )
            );

            lblTiempo.setForeground(Color.BLACK);
            lblTiempo.setAlignmentX(Component.CENTER_ALIGNMENT);

            p.add(Box.createVerticalStrut(4));
            p.add(lblTiempo);
        }

        p.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        p.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        new DialogSesionAlquiler(
                                NewJFrame.this,
                                nombre
                        ).setVisible(true);
                    }
                }
        );

        return p;
    }
}