package proyecto.com.Razonamiento_A_B.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.openxava.annotations.Hidden;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

@Entity
@Table(name = "evaluadores")
@View(members =
        "Datos del evaluador {" +
                "nombres, apellidos;" +
                "fechaNacimiento, sexo;" +
                "profesion" +
                "}"
)
@Tab(properties = "idEvaluador, nombres, apellidos, profesion")
public class Evaluador extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluador")
    @Hidden
    private Integer idEvaluador;

    @NotBlank(message = "La profesión es obligatoria")
    @Size(max = 100, message = "La profesión no debe superar 100 caracteres")
    @Column(length = 100, nullable = false)
    private String profesion;

    @Override
    public String obtenerRolSistema() {
        return "Evaluador";
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