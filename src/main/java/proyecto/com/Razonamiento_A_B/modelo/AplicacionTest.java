package proyecto.com.Razonamiento_A_B.modelo;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import proyecto.com.Razonamiento_A_B.enums.EstadoAplicacion;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "aplicaciones_test")
@View(members =
        "Datos de aplicación { evaluado; evaluador; testRazonamiento } " +
                "Control de estado { estado; fechaInicio; fechaFin } " +
                "Respuestas { respuestasMarcadas } " +
                "Resultado { resultado }"
)
@Tab(properties = "evaluado.nombres, evaluado.apellidos, evaluador.nombres, evaluador.apellidos, testRazonamiento.nombre, estado, fechaInicio, fechaFin")
public class AplicacionTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private int idAplicacion;

    @NotNull(message = "El estado de la aplicación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private EstadoAplicacion estado = EstadoAplicacion.PENDIENTE;

    @Column
    @ReadOnly
    private LocalDateTime fechaInicio;

    @Column
    @ReadOnly
    private LocalDateTime fechaFin;

    @NotNull(message = "El evaluado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluado", nullable = false)
    @DescriptionsList(descriptionProperties = "nombres, apellidos")
    private Evaluado evaluado;

    @NotNull(message = "El evaluador es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluador", nullable = false)
    @DescriptionsList(descriptionProperties = "nombres, apellidos")
    private Evaluador evaluador;

    @NotNull(message = "El test de razonamiento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_test", nullable = false)
    @DescriptionsList(descriptionProperties = "nombre")
    private TestRazonamiento testRazonamiento;

    @OneToMany(mappedBy = "aplicacionTest", cascade = CascadeType.ALL, orphanRemoval = true)
    @ListProperties("itemRazonamiento.numero, itemRazonamiento.subFactor, opcionSeleccionada, estadoRespuesta")
    private Collection<RespuestaMarcada> respuestasMarcadas = new ArrayList<>();

    @OneToOne(mappedBy = "aplicacionTest", cascade = CascadeType.ALL, orphanRemoval = true)
    private ResultadoRazonamiento resultado;

    public void iniciarFormaA() {
        if (estado != EstadoAplicacion.PENDIENTE) {
            throw new IllegalStateException("La Forma A solo puede iniciar cuando la aplicación está pendiente.");
        }
        fechaInicio = LocalDateTime.now();
        estado = EstadoAplicacion.EN_FORMA_A;
    }

    public void finalizarFormaA() {
        if (estado != EstadoAplicacion.EN_FORMA_A) {
            throw new IllegalStateException("La Forma A solo puede finalizar si está en progreso.");
        }
        estado = EstadoAplicacion.FINALIZADO_FORMA_A;
    }

    public void iniciarFormaB() {
        if (estado != EstadoAplicacion.FINALIZADO_FORMA_A) {
            throw new IllegalStateException("La Forma B solo puede iniciar después de finalizar la Forma A.");
        }
        estado = EstadoAplicacion.EN_FORMA_B;
    }

    public void finalizarAplicacion() {
        if (estado != EstadoAplicacion.EN_FORMA_B && estado != EstadoAplicacion.FINALIZADO_FORMA_A) {
            throw new IllegalStateException("La aplicación solo puede finalizar después de avanzar en el proceso del test.");
        }
        fechaFin = LocalDateTime.now();
        estado = EstadoAplicacion.FINALIZADO;
    }

    public int obtenerCantidadRespuestasMarcadas() {
        if (respuestasMarcadas == null) {
            return 0;
        }
        return respuestasMarcadas.size();
    }

    public int getIdAplicacion() {
        return idAplicacion;
    }

    public void setIdAplicacion(int idAplicacion) {
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

    public TestRazonamiento getTestRazonamiento() {
        return testRazonamiento;
    }

    public void setTestRazonamiento(TestRazonamiento testRazonamiento) {
        this.testRazonamiento = testRazonamiento;
    }

    public Collection<RespuestaMarcada> getRespuestasMarcadas() {
        return respuestasMarcadas;
    }

    public void setRespuestasMarcadas(Collection<RespuestaMarcada> respuestasMarcadas) {
        this.respuestasMarcadas = respuestasMarcadas;
    }

    public ResultadoRazonamiento getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoRazonamiento resultado) {
        this.resultado = resultado;
    }
}
