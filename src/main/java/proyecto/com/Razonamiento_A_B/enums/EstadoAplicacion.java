package proyecto.com.Razonamiento_A_B.enums;

public enum EstadoAplicacion {
    PENDIENTE("Pendiente"),
    EN_FORMA_A("En Forma A"),
    FINALIZADO_FORMA_A("Finalizado Forma A"),
    EN_FORMA_B("En Forma B"),
    FINALIZADO("Finalizado"),
    CALIFICADO("Calificado");

    private final String descripcion;

    EstadoAplicacion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}

