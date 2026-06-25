package proyecto.com.Razonamiento_A_B.modelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.openxava.annotations.CollectionView;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.Required;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

@Entity
@Table(name = "tests_razonamiento")
@Views({
        @View(members =
                "Datos del test { nombre; tiempoLimite; notaTiempo; } " +
                        "Instrucciones { instrucciones; } " +
                        "Ítems { items; }"
        ),

        @View(name = "Simple", members =
                "Datos del test { nombre; tiempoLimite; notaTiempo; }"
        )
})
@Tab(properties = "idTest,nombre,tiempoLimite")
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

    @Hidden
    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    @Required
    @NotBlank(message = "El nombre del test es obligatorio")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Required
    @NotNull(message = "El tiempo límite es obligatorio")
    @Min(value = 1, message = "El tiempo límite debe ser mayor a cero")
    @Column(name = "tiempo_limite", nullable = false)
    private Integer tiempoLimite;

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
    @Stereotype("MEMO")
    public String getNotaTiempo() {
        if (nombre == null || tiempoLimite == null) {
            return "Ingrese el nombre del test y su tiempo límite.";
        }

        return nombre.trim()
                + " tiene un tiempo límite de "
                + tiempoLimite
                + " minutos. Este tiempo se respetará tanto si se aplica solo como si se aplica dentro de una lista de tests.";
    }

    public List<ItemRazonamiento> cargarItems() {
        return items;
    }

    public String obtenerInstrucciones() {
        return instrucciones == null ? "" : instrucciones.trim();
    }

    public int obtenerTiempoLimite() {
        return tiempoLimite == null ? 0 : tiempoLimite;
    }

    public boolean esFormaA() {
        return "A".equalsIgnoreCase(codigo)
                || "RAZONAMIENTO_A".equalsIgnoreCase(codigo);
    }

    public boolean esFormaB() {
        return "B".equalsIgnoreCase(codigo)
                || "RAZONAMIENTO_B".equalsIgnoreCase(codigo);
    }

    public String obtenerNombreDescriptivo() {
        return nombre == null ? "" : nombre.trim();
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

        if (tiempoLimite == null || tiempoLimite <= 0) {
            throw new IllegalArgumentException("El tiempo límite debe ser mayor a cero");
        }

        if (instrucciones == null || instrucciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las instrucciones son obligatorias");
        }

        nombre = nombre.trim();
        instrucciones = instrucciones.trim();

        if (codigo == null || codigo.trim().isEmpty()) {
            codigo = generarCodigoAutomatico(nombre);
        } else {
            codigo = codigo.trim().toUpperCase();
        }
    }

    private String generarCodigoAutomatico(String nombreTest) {
        String nombreNormalizado = normalizarTexto(nombreTest);

        if ("RAZONAMIENTO_A".equals(nombreNormalizado)) {
            return "A";
        }

        if ("RAZONAMIENTO_B".equals(nombreNormalizado)) {
            return "B";
        }

        String sufijo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        return nombreNormalizado + "_" + sufijo;
    }

    private String normalizarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "TEST";
        }

        String normalizado = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD);
        normalizado = normalizado.replaceAll("\\p{M}", "");
        normalizado = normalizado.replaceAll("[^a-zA-Z0-9]+", "_");
        normalizado = normalizado.replaceAll("_+", "_");
        normalizado = normalizado.replaceAll("^_|_$", "");

        if (normalizado.isEmpty()) {
            return "TEST";
        }

        return normalizado.toUpperCase();
    }
}