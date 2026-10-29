package modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Equipo {

    private int id;
    private String nombre;
    private String tipo;
    private String estado;
    private BigDecimal precioHora;
    private LocalDateTime finProgramado;

    public Equipo(int id, String nombre, String tipo,
                  String estado, BigDecimal precioHora,
                  LocalDateTime finProgramado) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = estado;
        this.precioHora = precioHora;
        this.finProgramado = finProgramado;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEstado() {
        return estado;
    }

    public BigDecimal getPrecioHora() {
        return precioHora;
    }

    public LocalDateTime getFinProgramado() {
        return finProgramado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setFinProgramado(LocalDateTime finProgramado) {
        this.finProgramado = finProgramado;
    }
}

