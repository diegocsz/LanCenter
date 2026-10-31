package modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketHistorial(
            int idTicket,
            int idSesion,
            String cliente,
            String dni,
            String equipo,
            String tipo,
            String empleado,
            LocalDateTime inicio,
            LocalDateTime fin,
            BigDecimal monto) {
}
