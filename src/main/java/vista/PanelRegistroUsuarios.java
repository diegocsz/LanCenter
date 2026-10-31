
package vista;

import dao.ClienteDAO;
import java.awt.*;
import java.sql.SQLException;
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

        JLabel titulo = new JLabel("REGISTRO DE USUARIOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(titulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(new Color(240, 240, 240));
        contenido.setBorder(new EmptyBorder(10, 30, 30, 30));

        JPanel panelCliente = new JPanel(new GridLayout(5, 2, 15, 15));
        panelCliente.setBackground(Color.WHITE);
        panelCliente.setBorder(new CompoundBorder(
                new TitledBorder(
                        new LineBorder(new Color(180, 180, 180)),
                        "Datos del Cliente"),
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

        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe completar DNI, Nombre y Apellido.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!dni.matches("\\d{8}")) {

            JOptionPane.showMessageDialog(
                    this,
                    "El DNI debe contener 8 digitos.",
                    "DNI invalido",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {

            ClienteDAO dao = new ClienteDAO();

            boolean registrado = dao.registrarCliente(
                    dni, nombre, apellido, telefono, correo
            );

            if (registrado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Cliente registrado correctamente",
                        "Registro exitoso",
                        JOptionPane.INFORMATION_MESSAGE
                );

                txtDni.setText("");
                txtNombre.setText("");
                txtApellido.setText("");
                txtTelefono.setText("");
                txtCorreo.setText("");

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo registrar el cliente.",
                        "Registro",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {

                JOptionPane.showMessageDialog(
                        this,
                        "El DNI ya se encuentra registrado.",
                        "Cliente duplicado",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Error al registrar: " + e.getMessage(),
                        "Error de MySQL",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private JLabel crearLabel(String texto) {

        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }
}
