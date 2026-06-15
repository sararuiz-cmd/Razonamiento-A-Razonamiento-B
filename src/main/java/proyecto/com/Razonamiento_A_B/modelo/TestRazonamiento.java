package proyecto.com.Razonamiento_A_B.modelo;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

@Entity
@Table(name = "tests_razonamiento")
@View(members =
        "Datos del test { nombre; tiempoFormaA; tiempoFormaB; } " +
                "Instrucciones { instruccionesFormaA; instruccionesFormaB; } " +
                "Ítems { items; }")
@Tab(properties = "idTest,nombre,tiempoFormaA,tiempoFormaB")
public class TestRazonamiento {

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_test")
    private Integer idTest;

    @Required
    @NotBlank(message = "El nombre del test es obligatorio")
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "instrucciones_forma_a", columnDefinition = "TEXT")
    private String instruccionesFormaA;

    @Column(name = "instrucciones_forma_b", columnDefinition = "TEXT")
    private String instruccionesFormaB;

    @Required
    @Min(value = 1, message = "El tiempo de la forma A debe ser mayor a cero")
    @Column(name = "tiempo_forma_a", nullable = false)
    private Integer tiempoFormaA = 12;

    @Required
    @Min(value = 1, message = "El tiempo de la forma B debe ser mayor a cero")
    @Column(name = "tiempo_forma_b", nullable = false)
    private Integer tiempoFormaB = 12;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @ListProperties("numero,enunciado,opcionA,opcionB,opcionC,opcionD,respuestaCorrecta,subFactor,tipoItem")
    private List<ItemRazonamiento> items = new ArrayList<>();

    public List<ItemRazonamiento> cargarItems() {
        return items;
    }

    public String obtenerInstrucciones() {
        String a = instruccionesFormaA == null ? "" : instruccionesFormaA.trim();
        String b = instruccionesFormaB == null ? "" : instruccionesFormaB.trim();
        return (a + "\n\n" + b).trim();
    }

    public String obtenerInstruccionesFormaA() {
        return instruccionesFormaA;
    }

    public String obtenerInstruccionesFormaB() {
        return instruccionesFormaB;
    }

    public int obtenerTiempoLimite() {
        return Math.max(tiempoFormaA == null ? 0 : tiempoFormaA, tiempoFormaB == null ? 0 : tiempoFormaB);
    }

    public int obtenerTiempoLimiteFormaA() {
        return tiempoFormaA == null ? 0 : tiempoFormaA;
    }

    public int obtenerTiempoLimiteFormaB() {
        return tiempoFormaB == null ? 0 : tiempoFormaB;
    }

    public void agregarItem(ItemRazonamiento item) {
        if (item == null) {
            return;
        }
        item.setTest(this);
        items.add(item);
    }

    @PrePersist
    @PreUpdate
    private void validarRegistro() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del test es obligatorio");
        }
        if (tiempoFormaA == null || tiempoFormaA <= 0) {
            throw new IllegalArgumentException("El tiempo de la forma A debe ser mayor a cero");
        }
        if (tiempoFormaB == null || tiempoFormaB <= 0) {
            throw new IllegalArgumentException("El tiempo de la forma B debe ser mayor a cero");
        }
    }

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

    public List<ItemRazonamiento> getItems() {
        return items;
    }

    public void setItems(List<ItemRazonamiento> items) {
        this.items = items;
    }
}