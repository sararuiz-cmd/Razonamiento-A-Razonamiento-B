package proyecto.com.Razonamiento_A_B.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import proyecto.com.Razonamiento_A_B.enums.NivelAcademico;

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