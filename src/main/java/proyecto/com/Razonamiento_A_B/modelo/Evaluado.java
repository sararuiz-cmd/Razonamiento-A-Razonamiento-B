package proyecto.com.Razonamiento_A_B.modelo;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import proyecto.com.Razonamiento_A_B.enums.NivelAcademico;

@Entity
@View(name = "Datos del evaluado", members =
        "nombres, apellidos; " +
                "identificacion, sexo; " +
                "fechaNacimiento, nivelAcademico"
)
@Tab(properties = "identificacion, nombres, apellidos, sexo, fechaNacimiento, nivelAcademico")
public class Evaluado extends Persona {

    private static final int EDAD_MINIMA = 14;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Integer idEvaluado;

    @Required
    @NotNull(message = "El nivel académico es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private NivelAcademico nivelAcademico;

    @PrePersist
    @PreUpdate
    private void validarAntesDeGuardar() {
        registrar();
        if (!validarEdad()) {
            throw new IllegalStateException("El evaluado debe tener al menos 14 años");
        }
    }

    public void actualizar() {
        validarAntesDeGuardar();
    }

    public boolean validarEdad() {
        return calcularEdad() >= EDAD_MINIMA;
    }

    public String obtenerHistorialResultados() {
        return "Consultar las aplicaciones y resultados asociados al evaluado " + obtenerNombreCompleto();
    }

    @Override
    public String obtenerRolSistema() {
        return "EVALUADO";
    }

    @Override
    public void registrar() {
        super.registrar();
        if (nivelAcademico == null) {
            throw new IllegalArgumentException("Debe seleccionar el nivel académico");
        }
    }

    public Integer getIdEvaluado() {
        return idEvaluado;
    }

    public void setIdEvaluado(Integer idEvaluado) {
        this.idEvaluado = idEvaluado;
    }

    public NivelAcademico getNivelAcademico() {
        return nivelAcademico;
    }

    public void setNivelAcademico(NivelAcademico nivelAcademico) {
        this.nivelAcademico = nivelAcademico;
    }
}
