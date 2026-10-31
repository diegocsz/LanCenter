package modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketInfo(
            int id,
            String cliente,
            String equipo,
            LocalDateTime inicio,
            LocalDateTime fin,
            BigDecimal monto) {
}
