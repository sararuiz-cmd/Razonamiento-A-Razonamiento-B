package proyecto.com.Razonamiento_A_B.modelo;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import proyecto.com.Razonamiento_A_B.enums.OpcionRespuesta;
import proyecto.com.Razonamiento_A_B.enums.SubFactor;
import proyecto.com.Razonamiento_A_B.enums.TipoItem;

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "items_razonamiento")
@View(members =
        "Datos del item { testRazonamiento; numero; tipoItem, subFactor } " +
                "Contenido { enunciado; opcionA; opcionB; opcionC; opcionD; respuestaCorrecta }"
)
@Tab(properties = "testRazonamiento.nombre, numero, tipoItem, subFactor, respuestaCorrecta")
public class ItemRazonamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private int idItem;

    @NotNull(message = "El número del ítem es obligatorio")
    @Min(value = 1, message = "El número mínimo del ítem es 1")
    @Max(value = 55, message = "El número máximo del ítem es 55")
    @Column(nullable = false)
    private Integer numero;

    @NotBlank(message = "El enunciado es obligatorio")
    @Stereotype("MEMO")
    @Column(length = 2000, nullable = false)
    private String enunciado;

    @NotBlank(message = "La opción A es obligatoria")
    @Size(max = 500, message = "La opción A no debe superar 500 caracteres")
    @Column(length = 500, nullable = false)
    private String opcionA;

    @NotBlank(message = "La opción B es obligatoria")
    @Size(max = 500, message = "La opción B no debe superar 500 caracteres")
    @Column(length = 500, nullable = false)
    private String opcionB;

    @NotBlank(message = "La opción C es obligatoria")
    @Size(max = 500, message = "La opción C no debe superar 500 caracteres")
    @Column(length = 500, nullable = false)
    private String opcionC;

    @NotBlank(message = "La opción D es obligatoria")
    @Size(max = 500, message = "La opción D no debe superar 500 caracteres")
    @Column(length = 500, nullable = false)
    private String opcionD;

    @NotNull(message = "La respuesta correcta es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(length = 1, nullable = false)
    private OpcionRespuesta respuestaCorrecta;

    @NotNull(message = "El subfactor es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private SubFactor subFactor;

    @NotNull(message = "El tipo de ítem es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private TipoItem tipoItem;

    @NotNull(message = "El test de razonamiento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_test", nullable = false)
    @DescriptionsList(descriptionProperties = "nombre")
    private TestRazonamiento testRazonamiento;

    @PrePersist
    @PreUpdate
    public void validarConsistenciaItem() {
        if (numero == null || numero < 1 || numero > 55) {
            throw new IllegalArgumentException("El número del ítem debe estar entre 1 y 55.");
        }

        if (respuestaCorrecta == null) {
            throw new IllegalArgumentException("La respuesta correcta debe ser A, B, C o D.");
        }

        if (subFactor == null) {
            throw new IllegalArgumentException("Debe indicar el subfactor del ítem.");
        }

        if (tipoItem == null) {
            throw new IllegalArgumentException("Debe indicar el tipo de ítem.");
        }
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
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

    public OpcionRespuesta getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(OpcionRespuesta respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public SubFactor getSubFactor() {
        return subFactor;
    }

    public void setSubFactor(SubFactor subFactor) {
        this.subFactor = subFactor;
    }

    public TipoItem getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(TipoItem tipoItem) {
        this.tipoItem = tipoItem;
    }

    public TestRazonamiento getTestRazonamiento() {
        return testRazonamiento;
    }

    public void setTestRazonamiento(TestRazonamiento testRazonamiento) {
        this.testRazonamiento = testRazonamiento;
    }
}