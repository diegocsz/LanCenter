package modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Visita(
            int id,
            String cliente,
            String dni,
            String empleado,
            String tipo,
            String equipo,
            LocalDateTime inicio,
            LocalDateTime finProgramada,
            LocalDateTime finReal,
            BigDecimal precioHora,
            BigDecimal monto,
            String estado,
            Integer ticket) {
}
