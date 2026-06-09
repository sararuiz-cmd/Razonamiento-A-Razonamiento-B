package proyecto.com.Razonamiento_A_B.enums;

public enum NivelAcademico {
    PRIMARIA("Primaria"),
    SECUNDARIA("Secundaria"),
    TECNICO("Técnico"),
    UNIVERSITARIO("Universitario"),
    POSGRADO("Posgrado");

    private final String descripcion;

    NivelAcademico(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
