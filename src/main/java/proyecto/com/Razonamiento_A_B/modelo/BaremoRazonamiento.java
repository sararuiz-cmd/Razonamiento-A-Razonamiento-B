package proyecto.com.Razonamiento_A_B.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

import proyecto.com.Razonamiento_A_B.enums.Factor;

@Entity
@Table(
        name = "baremos_razonamiento",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_baremo_factor_rango",
                columnNames = {"factor", "puntaje_min", "puntaje_max"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@View(members =
        "Datos del baremo { factor; puntajeMin; puntajeMax; percentil }"
)
@Tab(properties = "idBaremo, factor, puntajeMin, puntajeMax, percentil")
public class BaremoRazonamiento {

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_baremo")
    private Long idBaremo;

    @Required
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "factor", nullable = false, length = 10)
    private Factor factor;

    @Required
    @NotNull
    @Min(0)
    @Column(name = "puntaje_min", nullable = false)
    private Integer puntajeMin;

    @Required
    @NotNull
    @Min(0)
    @Column(name = "puntaje_max", nullable = false)
    private Integer puntajeMax;

    @Required
    @NotNull
    @Min(1)
    @Max(99)
    @Column(name = "percentil", nullable = false)
    private Integer percentil;

    @PrePersist
    @PreUpdate
    private void validarRango() {
        if (puntajeMin == null || puntajeMax == null || percentil == null || factor == null) {
            throw new IllegalArgumentException("Todos los datos del baremo son obligatorios.");
        }

        if (puntajeMin > puntajeMax) {
            throw new IllegalArgumentException("El puntaje mínimo no puede ser mayor que el puntaje máximo.");
        }
    }
}