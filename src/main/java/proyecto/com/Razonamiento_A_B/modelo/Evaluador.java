package proyecto.com.Razonamiento_A_B.modelo;

import org.openxava.annotations.Hidden;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "evaluadores")
@View(name = "Simple", members =
        "Datos personales { nombres, apellidos; fechaNacimiento, sexo; profesion }"
)
@Tab(properties = "nombres, apellidos, sexo, profesion")
public class Evaluador extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private int idEvaluador;

    @NotBlank(message = "La profesión es obligatoria")
    @Size(max = 80, message = "La profesión no debe superar 80 caracteres")
    @Column(length = 80, nullable = false)
    private String profesion;

    @Override
    public String obtenerRolSistema() {
        return "Evaluador";
    }

    public int getIdEvaluador() {
        return idEvaluador;
    }

    public void setIdEvaluador(int idEvaluador) {
        this.idEvaluador = idEvaluador;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }
}
