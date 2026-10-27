package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Ticket {

    private final int idTicket;
    private final String nombreCliente;
    private final LocalDateTime horaInicio;
    private final LocalDateTime horaFin;
    private final LocalDate fechaTicket;
    private final BigDecimal monto;

    public Ticket(
            int idTicket,
            String nombreCliente,
            LocalDateTime horaInicio,
            LocalDateTime horaFin,
            LocalDate fechaTicket,
            BigDecimal monto) {

        this.idTicket = idTicket;
        this.nombreCliente = nombreCliente;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.fechaTicket = fechaTicket;
        this.monto = monto;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public LocalDateTime getHoraFin() {
        return horaFin;
    }

    public LocalDate getFechaTicket() {
        return fechaTicket;
    }

    public BigDecimal getMonto() {
        return monto;
    }
}