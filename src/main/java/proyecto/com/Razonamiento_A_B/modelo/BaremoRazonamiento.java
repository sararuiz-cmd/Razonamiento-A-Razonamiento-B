package proyecto.com.Razonamiento_A_B.modelo;

import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import proyecto.com.Razonamiento_A_B.enums.Factor;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "baremos_razonamiento")
@View(members = "factor; puntajeMin; puntajeMax; percentil")
@Tab(properties = "idBaremo,factor,puntajeMin,puntajeMax,percentil")

public class BaremoRazonamiento {
    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_baremo")
    private Integer idBaremo;

    @Required
    @NotNull(message = "El factor es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "factor", nullable = false, length = 10)
    private Factor factor;

    @Required
    @Min(value = 0, message = "El puntaje mínimo no puede ser negativo")
    @Column(name = "puntaje_min", nullable = false)
    private Integer puntajeMin = 0;

    @Required
    @Min(value = 0, message = "El puntaje máximo no puede ser negativo")
    @Column(name = "puntaje_max", nullable = false)
    private Integer puntajeMax = 0;

    @Required
    @Min(value = 0, message = "El percentil no puede ser negativo")
    @Column(name = "percentil", nullable = false)
    private Integer percentil = 0;

    public int buscarPercentil() {
        return percentil == null ? 0 : percentil;
    }

    public String interpretarNivel() {
        int p = buscarPercentil();
        if (p >= 75)  return "ALTO";
        if (p >= 40)  return "MEDIO";
        return "BAJO";
    }

    public List<BaremoRazonamiento> obtenerBaremosPorFactor() {
        return Collections.singletonList(this);
    }

    public boolean contienePuntaje(int puntaje) {
        return puntajeMin != null && puntajeMax != null && puntaje >= puntajeMin && puntaje <= puntajeMax;
    }

    @PrePersist
    @PreUpdate
    private void validarRegistro() {
        if (factor == null) {
            throw new IllegalArgumentException("El factor es obligatorio");
        }
        if (puntajeMin == null || puntajeMin < 0) {
            throw new IllegalArgumentException("El puntaje mínimo no puede ser negativo");
        }
        if (puntajeMax == null || puntajeMax < 0) {
            throw new IllegalArgumentException("El puntaje máximo no puede ser negativo");
        }
        if (puntajeMax < puntajeMin) {
            throw new IllegalArgumentException("El puntaje máximo no puede ser menor que el puntaje mínimo");
        }
        if (percentil == null || percentil < 0 || percentil > 100) {
            throw new IllegalArgumentException("El percentil debe estar entre 0 y 100");
        }
    }
}
