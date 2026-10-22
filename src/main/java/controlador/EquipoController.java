/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package controlador;

import dao.EquipoDAO;
import modelo.Equipo;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EquipoController {

    private final EquipoDAO equipoDAO;

    public EquipoController() {
        equipoDAO = new EquipoDAO();
    }

    // Obtener todos los equipos de MySQL
    public List<Equipo> listarEquipos() throws SQLException {
        return equipoDAO.listarEquipos();
    }

    // Calcular minutos restantes del alquiler
    public long obtenerMinutosRestantes(Equipo equipo) {

        if (equipo.getFinProgramado() == null) {
            return 0;
        }

        long segundos = Duration.between(
            LocalDateTime.now(),
            equipo.getFinProgramado()
        ).getSeconds();

        return Math.max(0, (segundos + 59) / 60);
    }

    // Comprobar disponibilidad
    public boolean estaDisponible(Equipo equipo) {
        return "Disponible".equals(equipo.getEstado());
    }

    // Obtener el color según el estado
    public String obtenerColorEstado(Equipo equipo) {

        switch (equipo.getEstado()) {
            case "Disponible":
                return "VERDE";

            case "Ocupada":
                return "ROJO";

            case "Mantenimiento":
                return "AMARILLO";

            default:
                return "GRIS";
        }
    }

    // Cambiar estado de mantenimiento
    public boolean cambiarMantenimiento(
            Equipo equipo, boolean mantenimiento)
            throws SQLException {

        String estadoActual = equipo.getEstado();

        if ("Ocupada".equals(estadoActual)) {
            throw new IllegalStateException(
                "No se puede modificar un equipo ocupado"
            );
        }

        String nuevoEstado = mantenimiento
                ? "Mantenimiento"
                : "Disponible";

        boolean actualizado = equipoDAO.cambiarEstado(
            equipo, nuevoEstado
        );

        if (actualizado) {
            equipo.setEstado(nuevoEstado);
        }

        return actualizado;
    }
}
