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
import javax.validation.constraints.NotBlank;
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
                "Datos académicos { nivelAcademico; } " +
                "Acceso al portal { usuario; contrasena; }")
@Tab(properties = "idEvaluado,nombres,apellidos,identificacion,usuario,fechaNacimiento,sexo,nivelAcademico")
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

    @Required
    @NotBlank(message = "El usuario del evaluado es obligatorio")
    @Column(name = "usuario", length = 50, unique = true)
    private String usuario;

    @Required
    @NotBlank(message = "La contraseña del evaluado es obligatoria")
    @Column(name = "contrasena", length = 100)
    private String contrasena;

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

    public boolean validarCredenciales(String usuarioIngresado, String contrasenaIngresada) {
        return usuario != null && contrasena != null
                && usuario.equalsIgnoreCase(usuarioIngresado == null ? "" : usuarioIngresado.trim())
                && contrasena.equals(contrasenaIngresada == null ? "" : contrasenaIngresada);
    }

    @PrePersist
    @PreUpdate
    private void validarRegistro() {
        validarDatosPersona();
        if (nivelAcademico == null) {
            throw new IllegalArgumentException("El nivel académico es obligatorio");
        }
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario del evaluado es obligatorio");
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña del evaluado es obligatoria");
        }
        usuario = usuario.trim();
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
