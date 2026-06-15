package proyecto.com.Razonamiento_A_B.modelo;

import java.util.Collections;
import java.util.List;
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
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import proyecto.com.Razonamiento_A_B.enums.NivelAcademico;

@Entity
@Table(name = "evaluados")
@View(members =
        "Datos personales { nombres; apellidos; identificacion; fechaNacimiento; sexo; } " +
                "Datos académicos { nivelAcademico; }")
@Tab(properties = "idEvaluado,nombres,apellidos,identificacion,fechaNacimiento,sexo,nivelAcademico")
public class Evaluado extends Persona {

    private static final int EDAD_MINIMA = 14;

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluado")
    private Integer idEvaluado;

    @Required
    @NotNull(message = "El nivel académico es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_academico", nullable = false, length = 30)
    private NivelAcademico nivelAcademico;

    public void actualizar() {
        validarRegistro();
    }

    public List<?> obtenerHistorialResultados() {
        return Collections.emptyList();
    }

    @Override
    public String obtenerRolSistema() {
        return "EVALUADO";
    }

    @Override
    public void registrar() {
        validarRegistro();
    }

    public boolean validarEdad() {
        return calcularEdad() >= EDAD_MINIMA;
    }

    @PrePersist
    @PreUpdate
    private void validarRegistro() {
        validarDatosPersona();
        if (nivelAcademico == null) {
            throw new IllegalArgumentException("El nivel académico es obligatorio");
        }
        if (!validarEdad()) {
            throw new IllegalArgumentException("El evaluado debe tener al menos " + EDAD_MINIMA + " años");
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
