package proyecto.com.Razonamiento_A_B.modelo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.openxava.annotations.CollectionView;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
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
@ToString(exclude = {"evaluado", "evaluador", "testsAplicados", "respuestasMarcadas"})
@Entity
@Table(name = "aplicaciontest")
@View(members =
        "Datos de aplicación { evaluado; evaluador; estado; } " +
                "Tests a aplicar { testsAplicados; }"
)
@Tab(properties = "idAplicacion,evaluado.nombres,evaluado.apellidos,evaluador.nombres,evaluador.apellidos,estado")
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
    @NotNull(message = "El estado de la aplicación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoAplicacion estado = EstadoAplicacion.PENDIENTE;

    @ManyToMany
    @JoinTable(
            name = "aplicaciontest_tests",
            joinColumns = @JoinColumn(name = "aplicacion_id"),
            inverseJoinColumns = @JoinColumn(name = "test_id")
    )
    @OrderColumn(name = "orden")
    @CollectionView("Simple")
    @ListProperties("nombre,tiempoLimite")
    private List<TestRazonamiento> testsAplicados = new ArrayList<>();

    @Hidden
    @Column(name = "indice_test_actual")
    private Integer indiceTestActual;

    @ReadOnly
    @Column(name = "fechainicio")
    private LocalDateTime fechaInicio;

    @ReadOnly
    @Column(name = "fechafin")
    private LocalDateTime fechaFin;

    @ReadOnly
    @Column(name = "fecha_inicio_test_actual")
    private LocalDateTime fechaInicioTestActual;

    @ReadOnly
    @Column(name = "fecha_fin_test_actual")
    private LocalDateTime fechaFinTestActual;

    @OneToMany(mappedBy = "aplicacionTest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespuestaMarcada> respuestasMarcadas = new ArrayList<>();

    public void iniciarAplicacion() {
        validarDatosBase();
        validarTestsAplicados();

        if (estado == EstadoAplicacion.FINALIZADA) {
            throw new IllegalStateException("No se puede iniciar una aplicación finalizada");
        }

        if (estado == EstadoAplicacion.CANCELADA) {
            throw new IllegalStateException("No se puede iniciar una aplicación cancelada");
        }

        fechaInicio = LocalDateTime.now();
        fechaFin = null;

        indiceTestActual = 0;
        fechaInicioTestActual = fechaInicio;
        fechaFinTestActual = null;

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
            if (marcada.getNumeroItem() != null && marcada.getNumeroItem().equals(numeroItem)) {
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

    public void finalizarTestActual() {
        if (estado != EstadoAplicacion.EN_CURSO) {
            throw new IllegalStateException("Solo se puede finalizar un test cuando la aplicación está en curso");
        }

        if (obtenerTestActualEntidad() == null) {
            throw new IllegalStateException("No hay un test activo para finalizar");
        }

        LocalDateTime ahora = LocalDateTime.now();
        fechaFinTestActual = ahora;

        if (haySiguienteTest()) {
            indiceTestActual = indiceTestActual + 1;
            fechaInicioTestActual = ahora;
            fechaFinTestActual = null;
        } else {
            fechaFin = ahora;
            estado = EstadoAplicacion.FINALIZADA;
        }
    }

    public void finalizarAplicacion() {
        if (estado != EstadoAplicacion.EN_CURSO && estado != EstadoAplicacion.PENDIENTE) {
            throw new IllegalStateException("La aplicación no puede finalizarse desde el estado actual");
        }

        LocalDateTime ahora = LocalDateTime.now();

        if (estado == EstadoAplicacion.PENDIENTE) {
            validarDatosBase();
            validarTestsAplicados();

            fechaInicio = ahora;
            fechaFin = ahora;
            fechaFinTestActual = ahora;
            estado = EstadoAplicacion.FINALIZADA;
            return;
        }

        if (haySiguienteTest()) {
            throw new IllegalStateException(
                    "No puede finalizar toda la aplicación todavía. Primero debe finalizar "
                            + getTestActual()
                            + " y continuar con el siguiente test."
            );
        }

        fechaFinTestActual = ahora;
        fechaFin = ahora;
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
        int total = 0;

        if (testsAplicados == null) {
            return total;
        }

        for (TestRazonamiento test : testsAplicados) {
            if (test != null) {
                total += test.obtenerTiempoLimite();
            }
        }

        return total;
    }

    @Transient
    @ReadOnly
    public String getTestActual() {
        TestRazonamiento actual = obtenerTestActualEntidad();

        if (actual == null) {
            return "";
        }

        return actual.obtenerNombreDescriptivo();
    }

    @Transient
    @ReadOnly
    public Integer getTiempoLimiteActual() {
        TestRazonamiento actual = obtenerTestActualEntidad();

        if (actual == null) {
            return null;
        }

        return actual.obtenerTiempoLimite();
    }

    @Transient
    @ReadOnly
    public Integer getTiempoRestanteActual() {
        TestRazonamiento actual = obtenerTestActualEntidad();

        if (actual == null || fechaInicioTestActual == null) {
            return null;
        }

        long segundosLimite = actual.obtenerTiempoLimite() * 60L;
        long segundosUsados = Duration.between(fechaInicioTestActual, LocalDateTime.now()).getSeconds();
        long segundosRestantes = segundosLimite - segundosUsados;

        if (segundosRestantes <= 0) {
            return 0;
        }

        return (int) Math.ceil(segundosRestantes / 60.0);
    }

    public List<ItemRazonamiento> obtenerItemsDelTestActual() {
        TestRazonamiento actual = obtenerTestActualEntidad();

        if (actual == null) {
            return new ArrayList<>();
        }

        return actual.cargarItems();
    }

    private TestRazonamiento obtenerTestActualEntidad() {
        if (testsAplicados == null || testsAplicados.isEmpty()) {
            return null;
        }

        if (indiceTestActual == null) {
            return null;
        }

        if (indiceTestActual < 0 || indiceTestActual >= testsAplicados.size()) {
            return null;
        }

        return testsAplicados.get(indiceTestActual);
    }

    private boolean haySiguienteTest() {
        if (testsAplicados == null || testsAplicados.isEmpty() || indiceTestActual == null) {
            return false;
        }

        return indiceTestActual < testsAplicados.size() - 1;
    }

    private void validarTiempoDisponible() {
        TestRazonamiento actual = obtenerTestActualEntidad();

        if (actual == null || fechaInicioTestActual == null) {
            return;
        }

        long segundosLimite = actual.obtenerTiempoLimite() * 60L;
        long segundosUsados = Duration.between(fechaInicioTestActual, LocalDateTime.now()).getSeconds();

        if (segundosLimite > 0 && segundosUsados >= segundosLimite) {
            String nombreTestVencido = actual.obtenerNombreDescriptivo();
            boolean tieneSiguiente = haySiguienteTest();

            finalizarTestActual();

            if (tieneSiguiente) {
                throw new IllegalStateException(
                        "El tiempo de "
                                + nombreTestVencido
                                + " ha finalizado. Ahora debe continuar con "
                                + getTestActual()
                                + "."
                );
            }

            throw new IllegalStateException(
                    "El tiempo de "
                            + nombreTestVencido
                            + " ha finalizado. La aplicación fue finalizada."
            );
        }
    }

    private void validarTiempoNoExcedido() {
        TestRazonamiento actual = obtenerTestActualEntidad();

        if (actual == null || fechaInicioTestActual == null || fechaFinTestActual == null) {
            return;
        }

        long segundosLimite = actual.obtenerTiempoLimite() * 60L;
        long segundosUsados = Duration.between(fechaInicioTestActual, fechaFinTestActual).getSeconds();

        if (segundosLimite > 0 && segundosUsados > segundosLimite) {
            throw new IllegalStateException(
                    "La duración de "
                            + actual.obtenerNombreDescriptivo()
                            + " no puede superar el tiempo límite de "
                            + actual.obtenerTiempoLimite()
                            + " minutos."
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
            indiceTestActual = null;
            fechaInicioTestActual = null;
            fechaFinTestActual = null;
        }

        if (estado == EstadoAplicacion.EN_CURSO) {
            validarTestsAplicados();

            if (fechaInicio == null) {
                fechaInicio = LocalDateTime.now();
            }

            if (indiceTestActual == null) {
                indiceTestActual = 0;
            }

            if (fechaInicioTestActual == null) {
                fechaInicioTestActual = fechaInicio;
            }

            fechaFin = null;
        }

        if (estado == EstadoAplicacion.FINALIZADA) {
            validarTestsAplicados();

            if (fechaInicio == null) {
                fechaInicio = LocalDateTime.now();
            }

            if (fechaFin == null) {
                fechaFin = LocalDateTime.now();
            }

            if (fechaFinTestActual == null) {
                fechaFinTestActual = fechaFin;
            }
        }
    }

    private void validarDatosBase() {
        if (evaluado == null) {
            throw new IllegalArgumentException("Debe seleccionar un evaluado");
        }

        if (evaluador == null) {
            throw new IllegalArgumentException("Debe seleccionar un evaluador");
        }
    }

    private void validarTestsAplicados() {
        if (testsAplicados == null || testsAplicados.isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un test a la aplicación");
        }

        Set<Integer> ids = new HashSet<>();

        for (TestRazonamiento test : testsAplicados) {
            if (test == null) {
                throw new IllegalArgumentException("La lista de tests no puede contener registros vacíos");
            }

            if (test.obtenerTiempoLimite() <= 0) {
                throw new IllegalArgumentException(
                        "El test "
                                + test.obtenerNombreDescriptivo()
                                + " debe tener un tiempo límite mayor a cero"
                );
            }

            if (test.getIdTest() != null && !ids.add(test.getIdTest())) {
                throw new IllegalArgumentException(
                        "No puede agregar el mismo test más de una vez en la misma aplicación"
                );
            }
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