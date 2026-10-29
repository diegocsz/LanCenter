package modelo;

import java.math.BigDecimal;

public class MetricasTickets {

    private final BigDecimal montoAcumulado;
    private final int sesionesAlquiladas;
    private final int equiposNormales;
    private final int equiposGamer;
    private final int consolas;
    private final long minutosAlquilados;
    private final BigDecimal promedioTicket;
    private final long duracionPromedioMinutos;
    private final BigDecimal ticketMayorMonto;

    public MetricasTickets(
            BigDecimal montoAcumulado,
            int sesionesAlquiladas,
            int equiposNormales,
            int equiposGamer,
            int consolas,
            long minutosAlquilados,
            BigDecimal promedioTicket,
            long duracionPromedioMinutos,
            BigDecimal ticketMayorMonto) {

        this.montoAcumulado = montoAcumulado;
        this.sesionesAlquiladas = sesionesAlquiladas;
        this.equiposNormales = equiposNormales;
        this.equiposGamer = equiposGamer;
        this.consolas = consolas;
        this.minutosAlquilados = minutosAlquilados;
        this.promedioTicket = promedioTicket;
        this.duracionPromedioMinutos = duracionPromedioMinutos;
        this.ticketMayorMonto = ticketMayorMonto;
    }

    public BigDecimal getMontoAcumulado() {
        return montoAcumulado;
    }

    public int getSesionesAlquiladas() {
        return sesionesAlquiladas;
    }

    public int getEquiposNormales() {
        return equiposNormales;
    }

    public int getEquiposGamer() {
        return equiposGamer;
    }

    public int getConsolas() {
        return consolas;
    }

    public long getMinutosAlquilados() {
        return minutosAlquilados;
    }

    public BigDecimal getPromedioTicket() {
        return promedioTicket;
    }

    public long getDuracionPromedioMinutos() {
        return duracionPromedioMinutos;
    }

    public BigDecimal getTicketMayorMonto() {
        return ticketMayorMonto;
    }
}