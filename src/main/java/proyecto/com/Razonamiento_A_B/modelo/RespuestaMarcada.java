package proyecto.com.Razonamiento_A_B.modelo;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import proyecto.com.Razonamiento_A_B.enums.EstadoRespuesta;
import proyecto.com.Razonamiento_A_B.enums.OpcionRespuesta;

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
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "respuestas_marcadas")
@View(members =
        "Aplicación { aplicacionTest } " +
                "Respuesta { itemRazonamiento; opcionSeleccionada; estadoRespuesta }"
)
@Tab(properties = "aplicacionTest.idAplicacion, itemRazonamiento.numero, itemRazonamiento.subFactor, opcionSeleccionada, estadoRespuesta")
public class RespuestaMarcada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private int idRespuesta;

    @NotNull(message = "La aplicación del test es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_aplicacion", nullable = false)
    @DescriptionsList(descriptionProperties = "idAplicacion")
    private AplicacionTest aplicacionTest;

    @NotNull(message = "El ítem de razonamiento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_item", nullable = false)
    @DescriptionsList(descriptionProperties = "numero, enunciado")
    private ItemRazonamiento itemRazonamiento;

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private OpcionRespuesta opcionSeleccionada;

    @NotNull(message = "El estado de respuesta es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoRespuesta estadoRespuesta = EstadoRespuesta.OMITIDA;

    @PrePersist
    @PreUpdate
    public void actualizarEstadoRespuesta() {
        estadoRespuesta = opcionSeleccionada == null ? EstadoRespuesta.OMITIDA : EstadoRespuesta.RESPONDIDA;
    }

    public boolean esCorrecta() {
        if (itemRazonamiento == null || itemRazonamiento.getRespuestaCorrecta() == null || opcionSeleccionada == null) {
            return false;
        }
        return opcionSeleccionada == itemRazonamiento.getRespuestaCorrecta();
    }

    public int getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(int idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public AplicacionTest getAplicacionTest() {
        return aplicacionTest;
    }

    public void setAplicacionTest(AplicacionTest aplicacionTest) {
        this.aplicacionTest = aplicacionTest;
    }

    public ItemRazonamiento getItemRazonamiento() {
        return itemRazonamiento;
    }

    public void setItemRazonamiento(ItemRazonamiento itemRazonamiento) {
        this.itemRazonamiento = itemRazonamiento;
    }

    public OpcionRespuesta getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    public void setOpcionSeleccionada(OpcionRespuesta opcionSeleccionada) {
        this.opcionSeleccionada = opcionSeleccionada;
    }

    public EstadoRespuesta getEstadoRespuesta() {
        return estadoRespuesta;
    }

    public void setEstadoRespuesta(EstadoRespuesta estadoRespuesta) {
        this.estadoRespuesta = estadoRespuesta;
    }
}
