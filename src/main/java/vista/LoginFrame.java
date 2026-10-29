package vista;

import dao.UsuarioDAO;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {
    private static final Color FONDO = new Color(43, 55, 66);
    private static final Color AZUL = new Color(47, 137, 211);
    private static final Color BLANCO = new Color(245, 246, 248);
    private final JTextField txtCorreo = new CampoCorreo();
    private final JPasswordField txtClave = new CampoClave();

    public LoginFrame() {
        setTitle("LAN CENTER - Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 550));
        setSize(980, 600);
        setLocationRelativeTo(null);

        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(FONDO);
        fondo.setBorder(new EmptyBorder(30, 35, 30, 35));
        setContentPane(fondo);

        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setOpaque(false);
        contenido.setPreferredSize(new Dimension(800, 460));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Bienvenido a LAN CENTER", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titulo.setForeground(BLANCO);
        c.gridy = 0;
        c.insets = new Insets(0, 0, 35, 0);
        contenido.add(titulo, c);

        JButton registro = botonAzul("Solicitar cuenta", 28);
        registro.setPreferredSize(new Dimension(0, 80));
        registro.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Solicita una cuenta al administrador de LAN CENTER.",
                "Solicitar cuenta", JOptionPane.INFORMATION_MESSAGE));
        c.gridy = 1;
        c.insets = new Insets(0, 0, 30, 0);
        contenido.add(registro, c);

        JPanel separador = new JPanel(new GridBagLayout());
        separador.setOpaque(false);
        GridBagConstraints s = new GridBagConstraints();
        s.gridy = 0;
        s.fill = GridBagConstraints.HORIZONTAL;
        s.weightx = 1;
        separador.add(linea(), s);
        JLabel o = new JLabel("  O  ");
        o.setForeground(BLANCO);
        o.setFont(new Font("Segoe UI", Font.BOLD, 22));
        s.gridx = 1;
        s.weightx = 0;
        separador.add(o, s);
        s.gridx = 2;
        s.weightx = 1;
        separador.add(linea(), s);
        c.gridy = 2;
        c.insets = new Insets(0, 0, 25, 0);
        contenido.add(separador, c);

        JLabel etiqueta = new JLabel("Iniciar sesión");
        etiqueta.setForeground(BLANCO);
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 17));
        c.gridy = 3;
        c.insets = new Insets(0, 0, 6, 0);
        contenido.add(etiqueta, c);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        formulario.setPreferredSize(new Dimension(0, 55));
        GridBagConstraints f = new GridBagConstraints();
        f.gridy = 0;
        f.fill = GridBagConstraints.BOTH;
        f.weighty = 1;
        f.weightx = 1;
        f.insets = new Insets(0, 0, 0, 10);
        configurarCampo(txtCorreo, "Correo electrónico");
        configurarCampo(txtClave, "Contraseña");
        f.gridx = 0;
        formulario.add(txtCorreo, f);
        f.gridx = 1;
        formulario.add(txtClave, f);
        JButton btnLogin = botonAzul("Iniciar sesión", 16);
        btnLogin.addActionListener(e -> login());
        f.gridx = 2;
        f.weightx = 0;
        f.insets = new Insets(0, 0, 0, 0);
        formulario.add(btnLogin, f);
        c.gridy = 4;
        c.insets = new Insets(0, 0, 0, 0);
        contenido.add(formulario, c);

        JLabel ayuda = new JLabel("¿Olvidaste tu contraseña? Contacta al administrador.");
        ayuda.setForeground(BLANCO);
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.gridy = 5;
        c.insets = new Insets(8, 0, 0, 0);
        contenido.add(ayuda, c);

        GridBagConstraints centro = new GridBagConstraints();
        centro.weightx = 1;
        centro.weighty = 1;
        fondo.add(contenido, centro);
        getRootPane().setDefaultButton(btnLogin);
    }

    private JPanel linea() {
        JPanel linea = new JPanel();
        linea.setBackground(BLANCO);
        linea.setPreferredSize(new Dimension(1, 3));
        return linea;
    }

    private void configurarCampo(JTextField campo, String ayuda) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        campo.setToolTipText(ayuda);
        campo.setBorder(new EmptyBorder(0, 15, 0, 15));
    }

    private JButton botonAzul(String texto, int tamano) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, tamano));
        boton.setBackground(AZUL);
        boton.setForeground(BLANCO);
        boton.setFocusPainted(false);
        boton.setBorder(new EmptyBorder(10, 18, 10, 18));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private void pintarAyuda(Graphics g, JTextField campo, String texto) {
        if (!campo.getText().isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(110, 110, 110));
        g2.setFont(campo.getFont());
        int y = (campo.getHeight() + g2.getFontMetrics().getAscent()
                - g2.getFontMetrics().getDescent()) / 2;
        g2.drawString(texto, 15, y);
        g2.dispose();
    }

    private class CampoCorreo extends JTextField {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            pintarAyuda(g, this, "Correo electrónico");
        }
    }

    private class CampoClave extends JPasswordField {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getPassword().length == 0) {
                pintarAyuda(g, this, "Contraseña");
            }
        }
    }

    private void login() {
        String correo = txtCorreo.getText().trim();
        char[] clave = txtClave.getPassword();
        if (correo.isEmpty() || clave.length == 0) {
            JOptionPane.showMessageDialog(this, "Ingrese su correo y contraseña.",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            java.util.Arrays.fill(clave, '\0');
            return;
        }
        try {
            UsuarioDAO.UsuarioAutenticado usuario =
                    new UsuarioDAO().autenticar(correo, new String(clave));
            if (usuario != null) {
                new NewJFrame(usuario).setVisible(true);
                dispose();
            } else {
                txtClave.setText("");
                JOptionPane.showMessageDialog(this,
                        "Correo o contraseña incorrectos, o cuenta desactivada.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo consultar la base de datos.\n" + e.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        } finally {
            java.util.Arrays.fill(clave, '\0');
        }
    }
}
