package vista;

import config.ConexionMySQL;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            if (!ConexionMySQL.verificarConexion()) {

                JOptionPane.showMessageDialog(
                        null,
                        "No se puede iniciar el sistema LAN Center.\n\n"
                        + "No hay conexión con la base de datos MySQL.\n"
                        + "Verifique que MySQL esté iniciado, que la base "
                        + "de datos exista y que las credenciales sean correctas.",
                        "Error de conexión",
                        JOptionPane.ERROR_MESSAGE
                );

                System.exit(1);
                return;
            }

            new LoginFrame().setVisible(true);
        });
    }
}