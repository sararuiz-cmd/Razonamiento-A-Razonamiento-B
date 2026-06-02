package proyecto.com.Razonamiento_A_B.modelo;

import proyecto.com.Razonamiento_A_B.enums.NivelAcademico;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "evaluados")
public class Evaluado extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluado")
    private Integer idEvaluado;

    @NotNull(message = "El nivel académico es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_academico", length = 30, nullable = false)
    private NivelAcademico nivelAcademico;

    @AssertTrue(message = "El evaluado debe tener 14 años o más para aplicar el test")
    public boolean isEdadMinimaValida() {
        return getFechaNacimiento() != null && getEdad() >= 14;
    }

    @Transient
    public boolean esElegibleParaAplicacion() {
        return getEdad() >= 14;
    }

    @Override
    @Transient
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