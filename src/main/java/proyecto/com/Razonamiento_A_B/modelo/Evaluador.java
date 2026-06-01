package proyecto.com.Razonamiento_A_B.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "evaluadores")
public class Evaluador extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluador")
    private Integer idEvaluador;

    @NotBlank(message = "La profesión es obligatoria")
    @Size(max = 100, message = "La profesión no debe superar 100 caracteres")
    @Column(length = 100, nullable = false)
    private String profesion;

    @Override
    @Transient
    public String getRolSistema() {
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