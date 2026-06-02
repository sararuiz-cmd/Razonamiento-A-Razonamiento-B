package proyecto.com.Razonamiento_A_B.modelo;

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
import javax.persistence.Transient;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

import org.openxava.annotations.Hidden;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

@Entity
@Table(name = "evaluados")
@View(members =
        "Datos del evaluado {" +
                "nombres, apellidos;" +
                "fechaNacimiento, sexo;" +
                "nivelAcademico" +
                "}"
)
@Tab(properties = "idEvaluado, nombres, apellidos, fechaNacimiento, nivelAcademico")
public class Evaluado extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluado")
    @Hidden
    private Integer idEvaluado;

    @NotNull(message = "El nivel académico es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_academico", length = 30, nullable = false)
    private NivelAcademico nivelAcademico;

    @PrePersist
    @PreUpdate
    public void validarEdadMinima() {
        if (getFechaNacimiento() == null || getEdad() < 14) {
            throw new ValidationException("El evaluado debe tener 14 años o más para aplicar el test.");
        }
    }

    @Transient
    public boolean esElegibleParaAplicacion() {
        return getEdad() >= 14;
    }

    @Override
    @Transient
    @Hidden
    public String getRolSistema() {
        return "Evaluado";
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