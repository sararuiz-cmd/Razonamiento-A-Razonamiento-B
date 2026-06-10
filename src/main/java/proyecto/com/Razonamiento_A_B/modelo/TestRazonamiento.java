package proyecto.com.Razonamiento_A_B.modelo;

import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "test_razonamiento")
@View(members =
        "Datos generales { nombre; tiempoFormaA, tiempoFormaB } " +
                "Instrucciones { instruccionesFormaA; instruccionesFormaB } " +
                "Items { items }"
)
@Tab(properties = "nombre, tiempoFormaA, tiempoFormaB")
public class TestRazonamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private int idTest;

    @NotBlank(message = "El nombre del test es obligatorio")
    @Size(max = 120, message = "El nombre no debe superar 120 caracteres")
    @Column(length = 120, nullable = false)
    private String nombre;

    @NotBlank(message = "Las instrucciones de la Forma A son obligatorias")
    @Stereotype("MEMO")
    @Column(length = 2000, nullable = false)
    private String instruccionesFormaA;

    @NotBlank(message = "Las instrucciones de la Forma B son obligatorias")
    @Stereotype("MEMO")
    @Column(length = 2000, nullable = false)
    private String instruccionesFormaB;

    @NotNull(message = "El tiempo de la Forma A es obligatorio")
    @Min(value = 1, message = "El tiempo de la Forma A debe ser mayor que cero")
    @Column(nullable = false)
    private Integer tiempoFormaA = 10;

    @NotNull(message = "El tiempo de la Forma B es obligatorio")
    @Min(value = 1, message = "El tiempo de la Forma B debe ser mayor que cero")
    @Column(nullable = false)
    private Integer tiempoFormaB = 12;

    @OneToMany(mappedBy = "testRazonamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @ListProperties("numero, tipoItem, subFactor, enunciado, respuestaCorrecta")
    private Collection<ItemRazonamiento> items = new ArrayList<>();

    public int getIdTest() {
        return idTest;
    }

    public void setIdTest(int idTest) {
        this.idTest = idTest;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public Collection<ItemRazonamiento> getItems() {
        return items;
    }

    public void setItems(Collection<ItemRazonamiento> items) {
        this.items = items;
    }
}