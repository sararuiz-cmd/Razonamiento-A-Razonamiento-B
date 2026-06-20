package proyecto.com.Razonamiento_A_B.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import proyecto.com.Razonamiento_A_B.enums.EstadoAplicacion;
import proyecto.com.Razonamiento_A_B.enums.OpcionRespuesta;

@Entity
@Table(name = "aplicaciontest")
@View(members =
        "Datos de aplicación { evaluado; evaluador; test; estado; } " +
                "Tiempo { fechaInicio; fechaFin; } " +
                "Respuestas { respuestasMarcadas; }")
@Tab(properties = "idAplicacion,evaluado.obtenerNombreCompleto,evaluador.obtenerNombreCompleto,test.nombre,estado,fechaInicio,fechaFin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"evaluado", "evaluador", "test", "respuestasMarcadas"})
public class AplicacionTest {

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idaplicacion")
    private Integer idAplicacion;

    @Required
    @NotNull(message = "El estado de la aplicación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoAplicacion estado = EstadoAplicacion.PENDIENTE;

    @Column(name = "fechainicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fechafin")
    private LocalDateTime fechaFin;

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
    @DescriptionsList(descriptionProperties = "nombre")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "testrazonamiento_id_test", nullable = false)
    private TestRazonamiento test;

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
    }

    public int obtenerTiempoEmpleado() {
        if (fechaInicio == null) {
            return 0;
        }
        LocalDateTime fin = fechaFin == null ? LocalDateTime.now() : fechaFin;
        return (int) Duration.between(fechaInicio, fin).toMinutes();
    }

    @PrePersist
    private void prepararRegistro() {
        if (estado == null) {
            estado = EstadoAplicacion.PENDIENTE;
        }
        validarDatosBase();
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

    public Integer getIdAplicacion() {
        return idAplicacion;
    }

    public void setIdAplicacion(Integer idAplicacion) {
        this.idAplicacion = idAplicacion;
    }

    public EstadoAplicacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAplicacion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Evaluado getEvaluado() {
        return evaluado;
    }

    public void setEvaluado(Evaluado evaluado) {
        this.evaluado = evaluado;
    }

    public Evaluador getEvaluador() {
        return evaluador;
    }

    public void setEvaluador(Evaluador evaluador) {
        this.evaluador = evaluador;
    }

    public TestRazonamiento getTest() {
        return test;
    }

    public void setTest(TestRazonamiento test) {
        this.test = test;
    }

    public List<RespuestaMarcada> getRespuestasMarcadas() {
        return respuestasMarcadas;
    }

    public void setRespuestasMarcadas(List<RespuestaMarcada> respuestasMarcadas) {
        this.respuestasMarcadas = respuestasMarcadas;
    }
}
