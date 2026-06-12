package proyecto.com.Razonamiento_A_B.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotBlank;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

@Entity
@View(name = "Datos del evaluador", members =
        "nombres, apellidos; " +
                "identificacion, sexo; " +
                "fechaNacimiento, profesion"
)
@Tab(properties = "identificacion, nombres, apellidos, sexo, profesion")
public class Evaluador extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Integer idEvaluador;

    @Required
    @NotBlank(message = "La profesión es obligatoria")
    @Column(length = 80, nullable = false)
    private String profesion;

    @PrePersist
    @PreUpdate
    private void validarAntesDeGuardar() {
        registrar();
        validarCredenciales();
    }

    public void validarCredenciales() {
        if (profesion == null || profesion.trim().isEmpty()) {
            throw new IllegalStateException("El evaluador debe tener una profesión registrada");
        }
    }

    @Override
    public String obtenerRolSistema() {
        return "EVALUADOR";
    }

    @Override
    public void registrar() {
        super.registrar();
        validarCredenciales();
    }

    public Integer getIdEvaluador() {
        return idEvaluador;
    }

    public void setIdEvaluador(Integer idEvaluador) {
        this.idEvaluador = idEvaluador;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }
}
