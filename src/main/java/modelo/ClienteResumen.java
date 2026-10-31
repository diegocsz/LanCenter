package modelo;

public record ClienteResumen(int id, String nombre, String dni) {

    @Override
    public String toString() {
        return nombre + " (" + dni + ")";
    }
}
