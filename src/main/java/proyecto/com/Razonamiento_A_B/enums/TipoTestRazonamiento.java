package proyecto.com.Razonamiento_A_B.enums;

public enum TipoTestRazonamiento {

    A("Razonamiento A", 10),
    B("Razonamiento B", 12);

    private final String descripcion;
    private final int tiempoMinutos;

    TipoTestRazonamiento(String descripcion, int tiempoMinutos) {
        this.descripcion = descripcion;
        this.tiempoMinutos = tiempoMinutos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getTiempoMinutos() {
        return tiempoMinutos;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}