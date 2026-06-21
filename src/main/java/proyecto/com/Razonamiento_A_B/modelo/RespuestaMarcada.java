package proyecto.com.Razonamiento_A_B.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import proyecto.com.Razonamiento_A_B.enums.EstadoRespuesta;
import proyecto.com.Razonamiento_A_B.enums.OpcionRespuesta;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "aplicacionTest")
@Entity
@Table(name = "aplicacion_respuestas")
@IdClass(RespuestaMarcada.RespuestaMarcadaId.class)
@View(members = "aplicacionTest; numeroItem; respuestaSeleccionada; estadoRespuesta; fechaRegistro")
@Tab(properties = "aplicacionTest.idAplicacion,numeroItem,respuestaSeleccionada,estadoRespuesta,fechaRegistro")
public class RespuestaMarcada {

    @Id
    @Required
    @DescriptionsList(descriptionProperties = "idAplicacion")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aplicaciontest_idaplicacion", nullable = false)
    private AplicacionTest aplicacionTest;

    @Id
    @Required
    @Min(value = 1, message = "El número del ítem debe ser mayor a cero")
    @Column(name = "numero_item", nullable = false)
    private Integer numeroItem;

    @Required
    @NotNull(message = "La respuesta seleccionada es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "respuesta_seleccionada", nullable = true, length = 1)
    private OpcionRespuesta respuestaSeleccionada;
    @Required
    @NotNull(message = "El estado de la respuesta es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_respuesta", nullable = false, length = 20)
    private EstadoRespuesta estadoRespuesta = EstadoRespuesta.RESPONDIDA;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public void marcarRespondida() {
        estadoRespuesta = EstadoRespuesta.RESPONDIDA;
        fechaRegistro = LocalDateTime.now();
    }

    public void marcarOmitida() {
        estadoRespuesta = EstadoRespuesta.OMITIDA;
        fechaRegistro = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    private void validarRegistro() {
        if (aplicacionTest == null) {
            throw new IllegalArgumentException("La respuesta debe pertenecer a una aplicación");
        }
        if (numeroItem == null || numeroItem <= 0) {
            throw new IllegalArgumentException("El número del ítem debe ser mayor a cero");
        }
        if (respuestaSeleccionada == null) {
            throw new IllegalArgumentException("La respuesta seleccionada es obligatoria");
        }
        if (estadoRespuesta == null) {
            estadoRespuesta = EstadoRespuesta.RESPONDIDA;
        }
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    public static class RespuestaMarcadaId implements Serializable {
        private Integer aplicacionTest;
        private Integer numeroItem;

        public RespuestaMarcadaId() {
        }

        public RespuestaMarcadaId(Integer aplicacionTest, Integer numeroItem) {
            this.aplicacionTest = aplicacionTest;
            this.numeroItem = numeroItem;
        }

        public Integer getAplicacionTest() {
            return aplicacionTest;
        }

        public void setAplicacionTest(Integer aplicacionTest) {
            this.aplicacionTest = aplicacionTest;
        }

        public Integer getNumeroItem() {
            return numeroItem;
        }

        public void setNumeroItem(Integer numeroItem) {
            this.numeroItem = numeroItem;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RespuestaMarcadaId)) return false;
            RespuestaMarcadaId that = (RespuestaMarcadaId) o;
            return Objects.equals(aplicacionTest, that.aplicacionTest)
                    && Objects.equals(numeroItem, that.numeroItem);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aplicacionTest, numeroItem);
        }
    }
}