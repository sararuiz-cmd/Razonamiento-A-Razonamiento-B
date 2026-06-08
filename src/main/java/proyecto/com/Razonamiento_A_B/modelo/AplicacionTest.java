package proyecto.com.Razonamiento_A_B.modelo;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import proyecto.com.Razonamiento_A_B.enums.EstadoAplicacion;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
public class AplicacionTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAplicacion;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    // BLOQUEAMOS EL SETTER A NIVEL DE LOMBOK PARA PROTEGER LA MÁQUINA DE ESTADOS
    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    private EstadoAplicacion estado = EstadoAplicacion.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    private Evaluado evaluado;

    @ManyToOne(fetch = FetchType.LAZY)
    private Evaluador evaluador;

    @ManyToOne(fetch = FetchType.LAZY)
    private TestRazonamiento testRazonamiento;

    @ElementCollection
    @CollectionTable(name="aplicacion_respuestas")
    @MapKeyColumn(name="numero_item")
    @Column(name="respuesta_seleccionada")
    private Map<Integer, String> respuestasMarcadas = new HashMap<>();

    }