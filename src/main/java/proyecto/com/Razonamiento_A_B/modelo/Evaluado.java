package proyecto.com.Razonamiento_A_B.modelo;

import org.openxava.annotations.Hidden;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import proyecto.com.Razonamiento_A_B.enums.NivelAcademico;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "evaluados")
@View(name = "Simple", members =
        "Datos personales { nombres, apellidos; fechaNacimiento, sexo; nivelAcademico }"
)
@Tab(properties = "nombres, apellidos, fechaNacimiento, sexo, nivelAcademico")
public class Evaluado extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private int idEvaluado;

    @NotNull(message = "El nivel académico es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private NivelAcademico nivelAcademico;

    @PrePersist
    @PreUpdate
    public void validarEdadMinima() {
        if (!esElegibleParaAplicacion()) {
            throw new IllegalArgumentException("El evaluado debe tener 14 años o más para realizar el test.");
        }
    }

    public boolean esElegibleParaAplicacion() {
        return calcularEdad() >= 14;
    }

    @Override
    public String obtenerRolSistema() {
        return "Evaluado";
    }

    public int getIdEvaluado() {
        return idEvaluado;
    }

    public void setIdEvaluado(int idEvaluado) {
        this.idEvaluado = idEvaluado;
    }

    public NivelAcademico getNivelAcademico() {
        return nivelAcademico;
    }

    public void setNivelAcademico(NivelAcademico nivelAcademico) {
        this.nivelAcademico = nivelAcademico;
    }
}
