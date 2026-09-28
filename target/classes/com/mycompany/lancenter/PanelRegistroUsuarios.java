/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lancenter;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.*;

public class PanelRegistroUsuarios extends JPanel {

    // =========================
    // CAMPOS DEL CLIENTE
    // =========================
    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtTelefono;
    private JTextField txtCorreo;

    // =========================
    // CAMPOS DE LA SESIÓN
    // =========================
    private JComboBox<String> cbEmpleado;
    private JComboBox<String> cbTipoEquipo;
    private JComboBox<String> cbEquipo;

    private JLabel lblTarifa;
    private JLabel lblHoraInicio;

    private JButton btnIniciarSesion;

    public PanelRegistroUsuarios() {

        // =========================
        // CONFIGURACIÓN PRINCIPAL
        // =========================
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // =========================
        // TÍTULO
        // =========================
        JLabel titulo = new JLabel("REGISTRO DE USUARIOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setBorder(new EmptyBorder(20, 20, 20, 20));

        add(titulo, BorderLayout.NORTH);

        // =========================
        // PANEL CONTENIDO
        // =========================
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(new Color(240, 240, 240));
        contenido.setBorder(new EmptyBorder(10, 30, 30, 30));

        // =====================================================
        // DATOS DEL CLIENTE
        // =====================================================
        JPanel panelCliente = new JPanel();

        panelCliente.setLayout(
                new GridLayout(5, 2, 15, 15)
        );

        panelCliente.setBackground(Color.WHITE);

        panelCliente.setBorder(
                new CompoundBorder(
                        new TitledBorder(
                                new LineBorder(
                                        new Color(180, 180, 180)
                                ),
                                "Datos del Cliente"
                        ),
                        new EmptyBorder(20, 20, 20, 20)
                )
        );

        // Crear campos
        txtDni = new JTextField();
        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();

        // DNI
        panelCliente.add(
                crearLabel("DNI:")
        );

        panelCliente.add(txtDni);

        // Nombre
        panelCliente.add(
                crearLabel("Nombre:")
        );

        panelCliente.add(txtNombre);

        // Apellido
        panelCliente.add(
                crearLabel("Apellido:")
        );

        panelCliente.add(txtApellido);

        // Teléfono
        panelCliente.add(
                crearLabel("Teléfono:")
        );

        panelCliente.add(txtTelefono);

        // Correo
        panelCliente.add(
                crearLabel("Correo:")
        );

        panelCliente.add(txtCorreo);

        // =====================================================
        // SESIÓN DE ALQUILER
        // =====================================================
        JPanel panelSesion = new JPanel();

        panelSesion.setLayout(
                new GridLayout(5, 2, 15, 15)
        );

        panelSesion.setBackground(Color.WHITE);

        panelSesion.setBorder(
                new CompoundBorder(
                        new TitledBorder(
                                new LineBorder(
                                        new Color(180, 180, 180)
                                ),
                                "Sesión de Alquiler"
                        ),
                        new EmptyBorder(20, 20, 20, 20)
                )
        );

        // =====================================================
        // EMPLEADO
        // Después vendrán desde MySQL
        // =====================================================
        cbEmpleado = new JComboBox<>();

        cbEmpleado.addItem(
                "Seleccionar empleado"
        );

        cbEmpleado.addItem(
                "Empleado 1"
        );

        cbEmpleado.addItem(
                "Empleado 2"
        );

        // =====================================================
        // TIPO DE EQUIPO
        // =====================================================
        cbTipoEquipo = new JComboBox<>();

        cbTipoEquipo.addItem(
                "PC Gamer"
        );

        cbTipoEquipo.addItem(
                "PC Normal"
        );

        cbTipoEquipo.addItem(
                "Consola"
        );

        // =====================================================
        // EQUIPO
        // Después vendrán desde MySQL
        // =====================================================
        cbEquipo = new JComboBox<>();

        cbEquipo.addItem(
                "Seleccionar equipo"
        );

        // =====================================================
        // TARIFA
        // =====================================================
        lblTarifa = new JLabel(
                "S/ 0.00"
        );

        lblTarifa.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        // =====================================================
        // HORA DE INICIO
        // =====================================================
        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                );

        lblHoraInicio = new JLabel(
                LocalDateTime.now().format(formato)
        );

        lblHoraInicio.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );

        // =====================================================
        // AGREGAR CAMPOS DE SESIÓN
        // =====================================================

        // Empleado
        panelSesion.add(
                crearLabel("Empleado:")
        );

        panelSesion.add(cbEmpleado);

        // Tipo equipo
        panelSesion.add(
                crearLabel("Tipo de equipo:")
        );

        panelSesion.add(cbTipoEquipo);

        // Equipo
        panelSesion.add(
                crearLabel("Equipo:")
        );

        panelSesion.add(cbEquipo);

        // Tarifa
        panelSesion.add(
                crearLabel("Tarifa por hora:")
        );

        panelSesion.add(lblTarifa);

        // Hora inicio
        panelSesion.add(
                crearLabel("Hora de inicio:")
        );

        panelSesion.add(lblHoraInicio);

        // =====================================================
        // BOTÓN INICIAR SESIÓN
        // =====================================================
        btnIniciarSesion =
                new JButton("INICIAR SESIÓN");

        btnIniciarSesion.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        btnIniciarSesion.setForeground(
                Color.WHITE
        );

        btnIniciarSesion.setBackground(
                new Color(33, 150, 243)
        );

        btnIniciarSesion.setFocusPainted(
                false
        );

        btnIniciarSesion.setPreferredSize(
                new Dimension(200, 50)
        );

        // Panel para centrar botón
        JPanel panelBoton =
                new JPanel();

        panelBoton.setOpaque(false);

        panelBoton.setBorder(
                new EmptyBorder(
                        20,
                        0,
                        0,
                        0
                )
        );

        panelBoton.add(
                btnIniciarSesion
        );

        // =====================================================
        // AGREGAR TODO AL CONTENIDO
        // =====================================================
        contenido.add(
                panelCliente
        );

        contenido.add(
                Box.createVerticalStrut(20)
        );

        contenido.add(
                panelSesion
        );

        contenido.add(
                panelBoton
        );

        // =====================================================
        // AGREGAR CONTENIDO AL PANEL PRINCIPAL
        // =====================================================
        add(
                contenido,
                BorderLayout.CENTER
        );

        // =====================================================
        // EVENTO DEL BOTÓN
        // =====================================================
        btnIniciarSesion.addActionListener(
                e -> iniciarSesion()
        );
    }

    // =========================================================
    // MÉTODO INICIAR SESIÓN
    // =========================================================
    private void iniciarSesion() {

        String dni =
                txtDni.getText();

        String nombre =
                txtNombre.getText();

        String apellido =
                txtApellido.getText();

        String telefono =
                txtTelefono.getText();

        String correo =
                txtCorreo.getText();

        // Verificar campos obligatorios
        if (dni.isEmpty()
                || nombre.isEmpty()
                || apellido.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe completar DNI, Nombre y Apellido.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Verificar empleado
        if (cbEmpleado.getSelectedIndex() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un empleado.",
                    "Empleado",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Verificar equipo
        if (cbEquipo.getSelectedIndex() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un equipo.",
                    "Equipo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // =====================================================
        // POR AHORA SOLO MOSTRAMOS LOS DATOS
        // Después aquí insertaremos en MySQL
        // =====================================================

        String mensaje =
                "Sesión iniciada correctamente\n\n"
                + "Cliente: "
                + nombre
                + " "
                + apellido
                + "\n"
                + "DNI: "
                + dni
                + "\n"
                + "Teléfono: "
                + telefono
                + "\n"
                + "Correo: "
                + correo
                + "\n\n"
                + "Empleado: "
                + cbEmpleado.getSelectedItem()
                + "\n"
                + "Tipo de equipo: "
                + cbTipoEquipo.getSelectedItem()
                + "\n"
                + "Equipo: "
                + cbEquipo.getSelectedItem()
                + "\n"
                + "Tarifa: "
                + lblTarifa.getText()
                + "\n"
                + "Hora inicio: "
                + lblHoraInicio.getText();

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Sesión iniciada",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // MÉTODO PARA CREAR LABELS
    // =========================================================
    private JLabel crearLabel(
            String texto
    ) {

        JLabel label =
                new JLabel(texto);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        return label;
    }
}