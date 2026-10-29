package vista;

import controlador.EquipoController;
import dao.UsuarioDAO;
import modelo.Equipo;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class NewJFrame extends JFrame {
    private static final Color ACTIVO = new Color(33, 150, 243);
    private static final Color NORMAL = new Color(55, 71, 79);
    private final UsuarioDAO.UsuarioAutenticado usuario;
    private final EquipoController equipos = new EquipoController();
    private final JPanel menu = new JPanel();
    private final List<JButton> botones = new ArrayList<>();
    private JButton btnEquipos;

    public NewJFrame(UsuarioDAO.UsuarioAutenticado usuario) {
        this.usuario = usuario;
        setTitle("Sistema de Gestión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        menu.setPreferredSize(new Dimension(190, 700));
        menu.setBackground(new Color(33, 43, 54));
        menu.setBorder(new EmptyBorder(30, 10, 30, 10));
        boolean admin = "ADMIN".equals(usuario.rol());
        menu.setLayout(new GridLayout(admin ? 4 : 3, 1, 0, 10));
        JButton btnUsuarios = boton("Registro Usuarios");
        btnEquipos = boton("Equipos");
        JButton btnTickets = boton("Tickets");
        btnUsuarios.addActionListener(e -> mostrar(btnUsuarios, new PanelRegistroUsuarios()));
        btnEquipos.addActionListener(e -> mostrar(btnEquipos, panelEquipos()));
        btnTickets.addActionListener(e -> mostrar(btnTickets, new PanelTickets()));
        if (admin) {
            JButton btnHistorial = boton("Historial de clientes");
            btnHistorial.addActionListener(e ->
                    mostrar(btnHistorial, new PanelHistorialClientes(usuario)));
        }
        mostrar(btnEquipos, panelEquipos());
    }

    private JButton boton(String texto) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setBackground(NORMAL);
        b.setBorder(new EmptyBorder(12, 8, 12, 8));
        menu.add(b);
        botones.add(b);
        return b;
    }

    private void mostrar(JButton activo, JPanel panel) {
        getContentPane().removeAll();
        add(menu, BorderLayout.WEST);
        add(panel, BorderLayout.CENTER);
        for (JButton b : botones) b.setBackground(b == activo ? ACTIVO : NORMAL);
        revalidate();
        repaint();
    }

    private JPanel panelEquipos() {
        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(new Color(240, 240, 240));
        JLabel titulo = new JLabel("EQUIPOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setBorder(new EmptyBorder(20, 20, 10, 20));
        principal.add(titulo, BorderLayout.NORTH);
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(new Color(240, 240, 240));
        try {
            List<Equipo> lista = equipos.listarEquipos();
            List<Equipo> pcs = lista.stream().filter(e -> !"Consola".equals(e.getTipo())).toList();
            List<Equipo> consolas = lista.stream().filter(e -> "Consola".equals(e.getTipo())).toList();
            contenido.add(grupo("Computadoras (" + pcs.size() + ")", pcs, 4));
            contenido.add(grupo("Consolas (" + consolas.size() + ")", consolas, 5));
        } catch (SQLException ex) {
            JLabel error = new JLabel("No se pudieron cargar los equipos: " + ex.getMessage());
            error.setBorder(new EmptyBorder(20, 20, 20, 20));
            contenido.add(error);
        }
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        principal.add(scroll, BorderLayout.CENTER);
        return principal;
    }

    private JPanel grupo(String titulo, List<Equipo> lista, int columnas) {
        JPanel grupo = new JPanel(new BorderLayout());
        grupo.setOpaque(false);
        JLabel encabezado = new JLabel(titulo, SwingConstants.CENTER);
        encabezado.setFont(new Font("Segoe UI", Font.BOLD, 20));
        encabezado.setBorder(new EmptyBorder(16, 0, 12, 0));
        grupo.add(encabezado, BorderLayout.NORTH);
        JPanel cuadricula = new JPanel(new GridLayout(0, columnas, 15, 15));
        cuadricula.setOpaque(false);
        cuadricula.setBorder(new EmptyBorder(0, 20, 20, 20));
        for (Equipo e : lista) cuadricula.add(tarjeta(e));
        grupo.add(cuadricula, BorderLayout.CENTER);
        return grupo;
    }

    private JPanel tarjeta(Equipo equipo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(130, 120));
        p.setBorder(new LineBorder(Color.DARK_GRAY));
        p.setBackground(switch (equipo.getEstado()) {
            case "Disponible" -> new Color(52, 177, 76);
            case "Ocupada" -> new Color(224, 68, 68);
            default -> new Color(235, 190, 57);
        });
        JLabel icono = new JLabel("Consola".equals(equipo.getTipo()) ? "🎮" : "🖥");
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel nombre = new JLabel(equipo.getNombre());
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel estado = new JLabel(equipo.getEstado());
        estado.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(Box.createVerticalGlue());
        p.add(icono);
        p.add(nombre);
        p.add(estado);
        if ("Ocupada".equals(equipo.getEstado()) && equipo.getFinProgramado() != null) {
            JLabel tiempo = new JLabel("Faltan " + equipos.obtenerMinutosRestantes(equipo) + " min");
            tiempo.setAlignmentX(Component.CENTER_ALIGNMENT);
            p.add(tiempo);
        }
        p.add(Box.createVerticalGlue());
        if (equipos.estaDisponible(equipo)) {
            p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            p.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent event) {
                    new DialogSesionAlquiler(NewJFrame.this, equipo, usuario.idEmpleado(),
                            () -> mostrar(btnEquipos, panelEquipos())).setVisible(true);
                }
            });
        }
        return p;
    }
}
