package proyecto.com.Razonamiento_A_B.modelo;

import proyecto.com.Razonamiento_A_B.modelo.ItemRazonamiento;

import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openxava.annotations.Hidden;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

@Entity
@Table(name = "tests_razonamiento")
@View(members =
        "Datos del Test {" +
                "nombre;" +
                "tiempoFormaA, tiempoFormaB" +
                "};" +
                "Instrucciones de Aplicación {" +
                "instruccionesFormaA;" +
                "instruccionesFormaB" +
                "};" +
                "Reactivos del Test {" +
                "items" +
                "}"
)
@Tab(properties = "idTest, nombre, tiempoFormaA, tiempoFormaB")
public class TestRazonamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_test")
    @Hidden
    private Integer idTest;

    @NotNull(message = "El nombre del test es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @NotNull(message = "El tiempo de la Forma A es obligatorio")
    @Column(name = "tiempo_forma_a", nullable = false)
    private Integer tiempoFormaA;

    @NotNull(message = "El tiempo de la Forma B es obligatorio")
    @Column(name = "tiempo_forma_b", nullable = false)
    private Integer tiempoFormaB;

    @Stereotype("MEMO")
    @Column(name = "instrucciones_forma_a", columnDefinition = "TEXT")
    private String instruccionesFormaA;

    @Stereotype("MEMO")
    @Column(name = "instrucciones_forma_b", columnDefinition = "TEXT")
    private String instruccionesFormaB;

    @OneToMany(mappedBy = "testRazonamiento", cascade = CascadeType.ALL)
    private Collection<ItemRazonamiento> items;

    // Getters y Setters
    public Integer getIdTest() {
        return idTest;
    }

    public void setIdTest(Integer idTest) {
        this.idTest = idTest;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getTiempoFormaA() {
        return tiempoFormaA;
    }

    public void setTiempoFormaA(Integer tiempoFormaA) {
        this.tiempoFormaA = tiempoFormaA;
    }

    public Integer getTiempoFormaB() {
        return tiempoFormaB;
    }

    public void setTiempoFormaB(Integer tiempoFormaB) {
        this.tiempoFormaB = tiempoFormaB;
    }

    public String getInstruccionesFormaA() {
        return instruccionesFormaA;
    }

    public void setInstruccionesFormaA(String instruccionesFormaA) {
        this.instruccionesFormaA = instruccionesFormaA;
    }

    public String getInstruccionesFormaB() {
        return instruccionesFormaB;
    }

    public void setInstruccionesFormaB(String instruccionesFormaB) {
        this.instruccionesFormaB = instruccionesFormaB;
    }

    public Collection<ItemRazonamiento> getItems() {
        return items;
    }

    public void setItems(Collection<ItemRazonamiento> items) {
        this.items = items;
    }
}