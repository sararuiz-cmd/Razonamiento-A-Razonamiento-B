package proyecto.com.Razonamiento_A_B.enums;

public enum Sexo {
    M("Masculino"),
    F("Femenino");
    
    private final String descripcion;

    Sexo(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
