package proyecto.com.Razonamiento_A_B.enums;

public enum SubFactor {
    R1("R1 - Forma A"),
    R2("R2 - Forma B");

    private final String descripcion;

    SubFactor(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}