package proyecto.com.Razonamiento_A_B.enums;

public enum EstadoRespuesta {
    RESPONDIDA("Respondida"),
    OMITIDA("Omitida");

    private final String descripcion;

    EstadoRespuesta(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
