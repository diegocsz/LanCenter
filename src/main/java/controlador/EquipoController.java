
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

    public List<Equipo> listarEquipos() throws SQLException {
        return equipoDAO.listarEquipos();
    }

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

    public boolean estaDisponible(Equipo equipo) {
        if ("Disponible".equals(equipo.getEstado())) {
            return true;
        }

        if ("Ocupada".equals(equipo.getEstado())
                && equipo.getFinProgramado() != null) {

            return !equipo.getFinProgramado()
                    .isAfter(LocalDateTime.now());
        }

        return false;
    }

    public String obtenerColorEstado(Equipo equipo) {
        if (estaDisponible(equipo)) {
            return "VERDE";
        }

        switch (equipo.getEstado()) {
            case "Ocupada":
                return "ROJO";

            case "Mantenimiento":
                return "AMARILLO";

            default:
                return "GRIS";
        }
    }

    public boolean cambiarMantenimiento(
            Equipo equipo, boolean mantenimiento)
            throws SQLException {

        String estadoActual = equipo.getEstado();

        if ("Ocupada".equals(estadoActual)
                && !estaDisponible(equipo)) {
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
