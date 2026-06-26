package proyecto.com.Razonamiento_A_B.modelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

import proyecto.com.Razonamiento_A_B.enums.OpcionRespuesta;
import proyecto.com.Razonamiento_A_B.enums.SubFactor;
import proyecto.com.Razonamiento_A_B.enums.TipoItem;

@Entity
@Table(
        name = "items_razonamiento",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_item_test_numero",
                columnNames = {"id_test_fk", "numero"}
        )
)
@Views({
        @View(members =
                "Datos del ítem { test; numero; enunciado; } " +
                        "Opciones { opcionA; opcionB; opcionC; opcionD; respuestaCorrecta; } " +
                        "Clasificación { subFactor; tipoItem; }"
        ),

        @View(name = "DesdeTest", members =
                "Datos del ítem { numero; enunciado; } " +
                        "Opciones { opcionA; opcionB; opcionC; opcionD; respuestaCorrecta; } " +
                        "Clasificación { subFactor; tipoItem; }"
        )
})
@Tab(properties = "idItem,test.tipoTest,numero,enunciado,respuestaCorrecta,subFactor,tipoItem")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "test")
public class ItemRazonamiento {

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Integer idItem;

    @Required
    @DescriptionsList(descriptionProperties = "tipoTest")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_test_fk", nullable = false)
    private TestRazonamiento test;

    @Required
    @Min(value = 1, message = "El número del ítem debe ser mayor a cero")
    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Required
    @NotBlank(message = "El enunciado es obligatorio")
    @Stereotype("MEMO")
    @Column(name = "enunciado", nullable = false, length = 1000)
    private String enunciado;

    @Required
    @NotBlank(message = "La opción A es obligatoria")
    @Column(name = "opcion_a", nullable = false, length = 500)
    private String opcionA;

    @Required
    @NotBlank(message = "La opción B es obligatoria")
    @Column(name = "opcion_b", nullable = false, length = 500)
    private String opcionB;

    @Required
    @NotBlank(message = "La opción C es obligatoria")
    @Column(name = "opcion_c", nullable = false, length = 500)
    private String opcionC;

    @Required
    @NotBlank(message = "La opción D es obligatoria")
    @Column(name = "opcion_d", nullable = false, length = 500)
    private String opcionD;

    @Required
    @NotNull(message = "La respuesta correcta es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "respuesta_correcta", nullable = false, length = 1)
    private OpcionRespuesta respuestaCorrecta;

    @Required
    @NotNull(message = "El subfactor es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "sub_factor", nullable = false, length = 10)
    private SubFactor subFactor;

    @Required
    @NotNull(message = "El tipo de ítem es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false, length = 40)
    private TipoItem tipoItem;

    public boolean verificarRespuesta(OpcionRespuesta respuesta) {
        return respuesta != null && respuestaCorrecta != null && respuestaCorrecta == respuesta;
    }

    public boolean verificarRespuesta(String respuesta) {
        if (respuesta == null || respuesta.trim().isEmpty()) {
            return false;
        }

        try {
            return verificarRespuesta(OpcionRespuesta.valueOf(respuesta.trim().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean verificarRespuesta() {
        return respuestaCorrecta != null;
    }

    @PrePersist
    @PreUpdate
    private void validarRegistro() {
        if (test == null) {
            throw new IllegalArgumentException("El ítem debe pertenecer a un test");
        }

        if (numero == null || numero <= 0) {
            throw new IllegalArgumentException("El número del ítem debe ser mayor a cero");
        }

        if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado es obligatorio");
        }

        if (opcionA == null || opcionA.trim().isEmpty()
                || opcionB == null || opcionB.trim().isEmpty()
                || opcionC == null || opcionC.trim().isEmpty()
                || opcionD == null || opcionD.trim().isEmpty()) {
            throw new IllegalArgumentException("Las opciones A, B, C y D son obligatorias");
        }

        if (respuestaCorrecta == null) {
            throw new IllegalArgumentException("La respuesta correcta es obligatoria");
        }

        if (subFactor == null) {
            throw new IllegalArgumentException("El subfactor es obligatorio");
        }

        if (tipoItem == null) {
            throw new IllegalArgumentException("El tipo de ítem es obligatorio");
        }
    }
}