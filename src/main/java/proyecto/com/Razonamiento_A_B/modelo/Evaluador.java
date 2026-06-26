package proyecto.com.Razonamiento_A_B.modelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

@Entity
@Table(name = "evaluadores")
@View(members =
        "Datos personales { nombres; apellidos; identificacion; fechaNacimiento; sexo; } " +
                "Datos profesionales { profesion; }"
)
@Tab(properties = "idEvaluador,nombres,apellidos,identificacion,fechaNacimiento,sexo,profesion")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Evaluador extends Persona {

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluador")
    private Integer idEvaluador;

    @Required
    @NotBlank(message = "La profesión es obligatoria")
    @Column(name = "profesion", nullable = false, length = 120)
    private String profesion;

    @Override
    public String obtenerRolSistema() {
        return "EVALUADOR";
    }

    @Override
    public void registrar() {
        validarCredenciales();
    }

    public void validarCredenciales() {
        validarDatosPersona();

        if (profesion == null || profesion.trim().isEmpty()) {
            throw new IllegalArgumentException("La profesión es obligatoria");
        }
    }

    @PrePersist
    @PreUpdate
    private void validarRegistro() {
        validarCredenciales();
    }
}