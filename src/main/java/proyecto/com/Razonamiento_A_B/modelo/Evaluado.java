package proyecto.com.Razonamiento_A_B.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
                "Acceso al portal { usuario; contrasena; }"
)
@Tab(properties = "idEvaluado,nombres,apellidos,identificacion,usuario,contrasena,fechaNacimiento,sexo,nivelAcademico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = "contrasena")
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
    @Column(name = "usuario", nullable = false, length = 50, unique = true)
    private String usuario;

    @Required
    @NotBlank(message = "La contraseña del evaluado es obligatoria")
    @Column(name = "contrasena", nullable = false, length = 64)
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
        String usuarioNormalizado = usuarioIngresado == null ? "" : usuarioIngresado.trim();
        String contrasenaCifrada = cifrarContrasena(contrasenaIngresada);

        return usuario != null
                && contrasena != null
                && usuario.equalsIgnoreCase(usuarioNormalizado)
                && contrasena.equals(contrasenaCifrada);
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
        cifrarContrasenaSiEsNecesario();

        if (!validarEdad()) {
            throw new IllegalArgumentException("El evaluado debe tener al menos " + EDAD_MINIMA + " años");
        }
    }

    private void cifrarContrasenaSiEsNecesario() {
        String valor = contrasena == null ? "" : contrasena.trim();

        if (!esHashSha256(valor)) {
            contrasena = cifrarContrasena(valor);
        } else {
            contrasena = valor;
        }
    }

    private static boolean esHashSha256(String valor) {
        return valor != null && valor.matches("^[a-fA-F0-9]{64}$");
    }

    public static String cifrarContrasena(String contrasenaPlano) {
        try {
            String valor = contrasenaPlano == null ? "" : contrasenaPlano.trim();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(valor.getBytes(StandardCharsets.UTF_8));

            StringBuilder resultado = new StringBuilder();

            for (byte b : hash) {
                resultado.append(String.format("%02x", b));
            }

            return resultado.toString();

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("No se pudo cifrar la contraseña", ex);
        }
    }
}