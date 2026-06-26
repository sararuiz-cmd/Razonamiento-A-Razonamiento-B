package proyecto.com.Razonamiento_A_B.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class Persona {

    @Required
    @NotBlank(message = "Los nombres son obligatorios")
    @Column(name = "nombres", nullable = false, length = 120)
    private String nombres;

    @Required
    @NotBlank(message = "Los apellidos son obligatorios")
    @Column(name = "apellidos", nullable = false, length = 120)
    private String apellidos;

    @Required
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Required
    @NotBlank(message = "La identificación es obligatoria")
    @Column(name = "identificacion", nullable = false, unique = true, length = 50)
    private String identificacion;

    @Required
    @NotNull(message = "El sexo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false, length = 1)
    private Sexo sexo;

    public String obtenerNombreCompleto() {
        String nom = nombres == null ? "" : nombres.trim();
        String ape = apellidos == null ? "" : apellidos.trim();
        return (nom + " " + ape).trim();
    }

    public int calcularEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public abstract String obtenerRolSistema();

    public void registrar() {
        validarDatosPersona();
    }

    protected void validarDatosPersona() {
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La identificación es obligatoria");
        }
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }
        if (sexo == null) {
            throw new IllegalArgumentException("El sexo debe ser F o M");
        }
    }

}
