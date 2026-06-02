package proyecto.com.Razonamiento_A_B.modelo;

import proyecto.com.Razonamiento_A_B.enums.TipoItem;
import proyecto.com.Razonamiento_A_B.enums.SubFactor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

@Entity
@Table(name = "items_razonamiento")
@View(members =
        "clasificacion { " +
                "numero, tipoItem, subFactor;" +
                "testRazonamiento" +
                "};" +
                "contenido {" +
                "enunciado" +
                "};" +
                "opcionesRespuesta {" +
                "opcionA, opcionB;" +
                "opcionC, opcionD;" +
                "respuestaCorrecta" +
                "}"
)
@Tab(properties = "numero, tipoItem, subFactor, enunciado, respuestaCorrecta")
public class ItemRazonamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    @Hidden
    private Integer idItem;

    @NotNull(message = "El número de ítem es obligatorio")
    @Column(name = "numero", nullable = false)
    private Integer numero;

    @NotNull(message = "El enunciado de la pregunta es obligatorio")
    @Size(max = 500, message = "El enunciado no puede exceder los 500 caracteres")
    @Column(name = "enunciado", length = 500, nullable = false)
    private String enunciado;

    @NotNull(message = "La opción A es obligatoria")
    @Size(max = 200, message = "La opción A no puede exceder los 200 caracteres")
    @Column(name = "opcion_a", length = 200, nullable = false)
    private String opcionA;

    @NotNull(message = "La opción B es obligatoria")
    @Size(max = 200, message = "La opción B no puede exceder los 200 caracteres")
    @Column(name = "opcion_b", length = 200, nullable = false)
    private String opcionB;

    @NotNull(message = "La opción C es obligatoria")
    @Size(max = 200, message = "La opción C no puede exceder los 200 caracteres")
    @Column(name = "opcion_c", length = 200, nullable = false)
    private String opcionC;

    @NotNull(message = "La opción D es obligatoria")
    @Size(max = 200, message = "La opción D no puede exceder los 200 caracteres")
    @Column(name = "opcion_d", length = 200, nullable = false)
    private String opcionD;

    @NotNull(message = "La respuesta correcta es obligatoria")
    @Size(max = 1, message = "La respuesta debe ser un único carácter")
    @Column(name = "respuesta_correcta", length = 1, nullable = false)
    private String respuestaCorrecta; // Cambiado a String para máxima estabilidad en UI

    @NotNull(message = "El tipo de ítem es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", length = 20, nullable = false)
    private TipoItem tipoItem;

    @NotNull(message = "El subfactor es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "sub_factor", length = 20, nullable = false)
    private SubFactor subFactor;

    @ManyToOne
    @JoinColumn(name = "id_test_fk", nullable = false)
    @DescriptionsList(descriptionProperties = "nombre")
    private TestRazonamiento testRazonamiento;

    @PrePersist
    @PreUpdate
    public void validarConsistenciaItem() {
        // Formatear y validar la respuesta correcta en String
        if (respuestaCorrecta != null) {
            respuestaCorrecta = respuestaCorrecta.toUpperCase().trim();
            if (!respuestaCorrecta.equals("A") && !respuestaCorrecta.equals("B") && !respuestaCorrecta.equals("C") && !respuestaCorrecta.equals("D")) {
                throw new ValidationException("La respuesta correcta debe ser una de las opciones válidas: A, B, C o D.");
            }
        }

        if (numero == null || numero < 1 || numero > 55) {
            throw new ValidationException("El número de ítem debe estar en el rango del instrumento (1 al 55).");
        }
    }

    // Getters y Setters
    public Integer getIdItem() {
        return idItem;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getOpcionA() {
        return opcionA;
    }

    public void setOpcionA(String opcionA) {
        this.opcionA = opcionA;
    }

    public String getOpcionB() {
        return opcionB;
    }

    public void setOpcionB(String opcionB) {
        this.opcionB = opcionB;
    }

    public String getOpcionC() {
        return opcionC;
    }

    public void setOpcionC(String opcionC) {
        this.opcionC = opcionC;
    }

    public String getOpcionD() {
        return opcionD;
    }

    public void setOpcionD(String opcionD) {
        this.opcionD = opcionD;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public TipoItem getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(TipoItem tipoItem) {
        this.tipoItem = tipoItem;
    }

    public SubFactor getSubFactor() {
        return subFactor;
    }

    public void setSubFactor(SubFactor subFactor) {
        this.subFactor = subFactor;
    }

    public TestRazonamiento getTestRazonamiento() {
        return testRazonamiento;
    }

    public void setTestRazonamiento(TestRazonamiento testRazonamiento) {
        this.testRazonamiento = testRazonamiento;
    }
}