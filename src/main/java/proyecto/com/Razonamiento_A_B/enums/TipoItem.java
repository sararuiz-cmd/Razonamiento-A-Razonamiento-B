package proyecto.com.Razonamiento_A_B.enums;

public enum TipoItem {
    NUMERICO("Numérico"),
    VERBAL("Verbal");

    private final String descripcion;

    TipoItem(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}