package proyecto.com.Razonamiento_A_B.modelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.openxava.annotations.CollectionView;
import org.openxava.annotations.Depends;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.Required;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

import proyecto.com.Razonamiento_A_B.enums.TipoTestRazonamiento;

@Entity
@Table(name = "tests_razonamiento")
@View(members =
        "Datos del test { tipoTest; tiempoLimite; notaTiempoBFA; } " +
                "Instrucciones { instrucciones; } " +
                "Ítems { items; }"
)
@Tab(properties = "idTest,tipoTest")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "items")
public class TestRazonamiento {

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_test")
    private Integer idTest;

    @Required
    @NotNull(message = "Debe seleccionar el tipo de test: A o B")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_test", nullable = false, length = 1)
    private TipoTestRazonamiento tipoTest = TipoTestRazonamiento.A;

    @Required
    @NotBlank(message = "Las instrucciones son obligatorias")
    @Stereotype("MEMO")
    @Column(name = "instrucciones", nullable = false, columnDefinition = "TEXT")
    private String instrucciones;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @CollectionView("DesdeTest")
    @ListProperties("numero,enunciado,opcionA,opcionB,opcionC,opcionD,respuestaCorrecta,subFactor,tipoItem")
    private List<ItemRazonamiento> items = new ArrayList<>();

    @Transient
    @ReadOnly
    @Depends("tipoTest")
    public Integer getTiempoLimite() {
        if (tipoTest == null) {
            return null;
        }

        return tipoTest.getTiempoMinutos();
    }

    @Transient
    @ReadOnly
    @Stereotype("MEMO")
    @Depends("tipoTest")
    public String getNotaTiempoBFA() {
        if (tipoTest == null) {
            return "Seleccione el tipo de test para mostrar el tiempo límite.";
        }

        return "Tiempo límite establecido por el manual BFA para "
                + tipoTest
                + ": "
                + tipoTest.getTiempoMinutos()
                + " minutos.";
    }

    public List<ItemRazonamiento> cargarItems() {
        return items;
    }

    public String obtenerInstrucciones() {
        return instrucciones == null ? "" : instrucciones.trim();
    }

    public int obtenerTiempoLimite() {
        return tipoTest == null ? 0 : tipoTest.getTiempoMinutos();
    }

    public boolean esFormaA() {
        return tipoTest == TipoTestRazonamiento.A;
    }

    public boolean esFormaB() {
        return tipoTest == TipoTestRazonamiento.B;
    }

    public String obtenerNombreDescriptivo() {
        return tipoTest == null ? "" : tipoTest.toString();
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
        if (tipoTest == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de test: A o B");
        }

        if (instrucciones == null || instrucciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las instrucciones son obligatorias");
        }
    }
}