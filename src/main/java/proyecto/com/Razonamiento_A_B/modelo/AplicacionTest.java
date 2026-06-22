package proyecto.com.Razonamiento_A_B.modelo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

import proyecto.com.Razonamiento_A_B.enums.EstadoAplicacion;
import proyecto.com.Razonamiento_A_B.enums.OpcionRespuesta;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"evaluado", "evaluador", "test", "respuestasMarcadas"})
@Entity
@Table(name = "aplicaciontest")
@View(members =
        "Datos de aplicación { evaluado; evaluador; test; estado; } " +
                "Respuestas { respuestasMarcadas; }"
)
@Tab(properties = "idAplicacion,evaluado.nombres,evaluado.apellidos,evaluador.nombres,evaluador.apellidos,test.tipoTest,estado,fechaInicio,fechaFin")
public class AplicacionTest {

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idaplicacion")
    private Integer idAplicacion;

    @Required
    @DescriptionsList(descriptionProperties = "nombres,apellidos")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluado_id_evaluado", nullable = false)
    private Evaluado evaluado;

    @Required
    @DescriptionsList(descriptionProperties = "nombres,apellidos")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluador_id_evaluador", nullable = false)
    private Evaluador evaluador;

    @Required
    @NoCreate
    @NoModify
    @DescriptionsList(descriptionProperties = "tipoTest")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "testrazonamiento_id_test", nullable = false)
    private TestRazonamiento test;

    @Required
    @NotNull(message = "El estado de la aplicación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoAplicacion estado = EstadoAplicacion.PENDIENTE;

    @ReadOnly
    @Column(name = "fechainicio")
    private LocalDateTime fechaInicio;

    @ReadOnly
    @Column(name = "fechafin")
    private LocalDateTime fechaFin;

    @OneToMany(mappedBy = "aplicacionTest", cascade = CascadeType.ALL, orphanRemoval = true)
    @ListProperties("numeroItem,respuestaSeleccionada,estadoRespuesta,fechaRegistro")
    private List<RespuestaMarcada> respuestasMarcadas = new ArrayList<>();

    public void iniciarAplicacion() {
        validarDatosBase();

        if (estado == EstadoAplicacion.FINALIZADA) {
            throw new IllegalStateException("No se puede iniciar una aplicación finalizada");
        }

        if (estado == EstadoAplicacion.CANCELADA) {
            throw new IllegalStateException("No se puede iniciar una aplicación cancelada");
        }

        fechaInicio = LocalDateTime.now();
        fechaFin = null;
        estado = EstadoAplicacion.EN_CURSO;
    }

    public void registrarRespuesta(String respuesta, int numeroItem) {
        if (estado != EstadoAplicacion.EN_CURSO) {
            throw new IllegalStateException("Solo se pueden registrar respuestas cuando la aplicación está en curso");
        }

        validarTiempoDisponible();

        if (numeroItem <= 0) {
            throw new IllegalArgumentException("El número del ítem debe ser mayor a cero");
        }

        OpcionRespuesta opcion = convertirRespuesta(respuesta);

        RespuestaMarcada encontrada = null;

        for (RespuestaMarcada marcada : respuestasMarcadas) {
            if (marcada.getNumeroItem() != null && marcada.getNumeroItem() == numeroItem) {
                encontrada = marcada;
                break;
            }
        }

        if (encontrada == null) {
            encontrada = new RespuestaMarcada();
            encontrada.setAplicacionTest(this);
            encontrada.setNumeroItem(numeroItem);
            respuestasMarcadas.add(encontrada);
        }

        encontrada.setRespuestaSeleccionada(opcion);
        encontrada.marcarRespondida();
    }

    public void finalizarAplicacion() {
        if (estado != EstadoAplicacion.EN_CURSO && estado != EstadoAplicacion.PENDIENTE) {
            throw new IllegalStateException("La aplicación no puede finalizarse desde el estado actual");
        }

        fechaFin = LocalDateTime.now();
        estado = EstadoAplicacion.FINALIZADA;

        validarTiempoNoExcedido();
    }

    public int obtenerTiempoEmpleado() {
        if (fechaInicio == null) {
            return 0;
        }

        LocalDateTime fin = fechaFin == null ? LocalDateTime.now() : fechaFin;
        return (int) Duration.between(fechaInicio, fin).toMinutes();
    }

    public int obtenerTiempoLimite() {
        return test == null ? 0 : test.obtenerTiempoLimite();
    }

    private void validarTiempoDisponible() {
        if (test == null || fechaInicio == null) {
            return;
        }

        int tiempoLimite = test.obtenerTiempoLimite();
        int tiempoEmpleado = obtenerTiempoEmpleado();

        if (tiempoLimite > 0 && tiempoEmpleado >= tiempoLimite) {
            finalizarAplicacion();
            throw new IllegalStateException("El tiempo límite del test ha finalizado");
        }
    }

    private void validarTiempoNoExcedido() {
        if (test == null || fechaInicio == null || fechaFin == null) {
            return;
        }

        int tiempoLimite = test.obtenerTiempoLimite();
        int tiempoEmpleado = obtenerTiempoEmpleado();

        if (tiempoLimite > 0 && tiempoEmpleado > tiempoLimite) {
            throw new IllegalStateException(
                    "La duración de la aplicación no puede superar el tiempo límite del test: "
                            + tiempoLimite + " minutos."
            );
        }
    }

    @PrePersist
    @PreUpdate
    private void prepararRegistro() {
        if (estado == null) {
            estado = EstadoAplicacion.PENDIENTE;
        }

        validarDatosBase();

        if (estado == EstadoAplicacion.PENDIENTE) {
            fechaInicio = null;
            fechaFin = null;
        }

        if (estado == EstadoAplicacion.EN_CURSO) {
            if (fechaInicio == null) {
                fechaInicio = LocalDateTime.now();
            }
            fechaFin = null;
        }

        if (estado == EstadoAplicacion.FINALIZADA) {
            if (fechaInicio == null) {
                fechaInicio = LocalDateTime.now();
            }
            if (fechaFin == null) {
                fechaFin = LocalDateTime.now();
            }
            validarTiempoNoExcedido();
        }
    }

    private void validarDatosBase() {
        if (evaluado == null) {
            throw new IllegalArgumentException("Debe seleccionar un evaluado");
        }

        if (evaluador == null) {
            throw new IllegalArgumentException("Debe seleccionar un evaluador");
        }

        if (test == null) {
            throw new IllegalArgumentException("Debe seleccionar un test");
        }
    }

    private OpcionRespuesta convertirRespuesta(String respuesta) {
        if (respuesta == null || respuesta.trim().isEmpty()) {
            throw new IllegalArgumentException("La respuesta seleccionada es obligatoria");
        }

        try {
            return OpcionRespuesta.valueOf(respuesta.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("La respuesta debe ser A, B, C o D");
        }
    }
}