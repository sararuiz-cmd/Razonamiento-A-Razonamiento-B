package proyecto.com.Razonamiento_A_B.modelo;

import java.time.LocalDate;
import java.time.Period;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.openxava.annotations.Required;
import proyecto.com.Razonamiento_A_B.enums.Sexo;

@MappedSuperclass
public abstract class Persona {

    @Required
    @NotBlank(message = "Los nombres son obligatorios")
    @Column(length = 80, nullable = false)
    private String nombres;

    @Required
    @NotBlank(message = "Los apellidos son obligatorios")
    @Column(length = 80, nullable = false)
    private String apellidos;

    @Required
    @NotBlank(message = "La identificación es obligatoria")
    @Column(length = 30, nullable = false, unique = true)
    private String identificacion;

    @Required
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Required
    @NotNull(message = "El sexo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(length = 1, nullable = false)
    private Sexo sexo;

    public int calcularEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public String obtenerNombreCompleto() {
        String n = nombres == null ? "" : nombres.trim();
        String a = apellidos == null ? "" : apellidos.trim();
        return (n + " " + a).trim();
    }

    public abstract String obtenerRolSistema();

    public void registrar() {
        validarDatosGenerales();
    }

    protected void validarDatosGenerales() {
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar los nombres");
        }
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar los apellidos");
        }
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar la identificación");
        }
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("Debe ingresar la fecha de nacimiento");
        }
        if (sexo == null) {
            throw new IllegalArgumentException("Debe seleccionar sexo F o M");
        }
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }
}
