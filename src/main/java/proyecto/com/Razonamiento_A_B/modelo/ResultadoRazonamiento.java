package proyecto.com.Razonamiento_A_B.modelo;

import lombok.Getter;
import lombok.Setter;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.Required;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;

import proyecto.com.Razonamiento_A_B.enums.Factor;
import proyecto.com.Razonamiento_A_B.servicio.BaremoRazonamientoService;

@Entity
@Getter
@Setter
@Table(name = "resultados_razonamiento")
@View(members =
        "DatosResultado { idResultado; aplicacionTest; } " +
                "Puntajes { r1; percentilR1; r2; percentilR2; rt; percentilRT; aciertos; } " +
                "Resumen { resumenFinal; }"
)
@Tab(properties =
        "idResultado, aplicacionTest.idAplicacion, r1, percentilR1, r2, percentilR2, rt, percentilRT, aciertos"
)
public class ResultadoRazonamiento {

    @Id
    @ReadOnly
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado")
    private Integer idResultado;

    @Required
    @DescriptionsList(descriptionProperties = "idAplicacion")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aplicacion_idaplicacion", nullable = false, unique = true)
    private AplicacionTest aplicacionTest;

    @Min(0)
    @Column(name = "r1")
    private Integer r1;

    @ReadOnly
    @Column(name = "percentil_r1")
    private Integer percentilR1;

    @Min(0)
    @Column(name = "r2")
    private Integer r2;

    @ReadOnly
    @Column(name = "percentil_r2")
    private Integer percentilR2;

    @ReadOnly
    @Column(name = "rt")
    private Integer rt;

    @ReadOnly
    @Column(name = "percentil_rt")
    private Integer percentilRT;

    @ReadOnly
    @Column(name = "aciertos")
    private Integer aciertos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baremo_r1_id")
    private BaremoRazonamiento baremoR1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baremo_r2_id")
    private BaremoRazonamiento baremoR2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baremo_rt_id")
    private BaremoRazonamiento baremoRT;

    @Transient
    @ReadOnly
    @Stereotype("MEMO")
    public String getResumenFinal() {
        return generarResumen();
    }

    public String generarResumen() {
        StringBuilder resumen = new StringBuilder();

        resumen.append("Resumen del resultado de razonamiento");
        resumen.append("\nId resultado: ")
                .append(idResultado == null ? "Pendiente de guardar" : idResultado);

        if (r1 != null) {
            resumen.append("\nR1: ").append(r1)
                    .append(" | Percentil: ").append(valorTexto(percentilR1));
        }

        if (r2 != null) {
            resumen.append("\nR2: ").append(r2)
                    .append(" | Percentil: ").append(valorTexto(percentilR2));
        }

        if (rt != null) {
            resumen.append("\nRT: ").append(rt)
                    .append(" | Percentil: ").append(valorTexto(percentilRT));
        }

        resumen.append("\nAciertos: ").append(aciertos == null ? 0 : aciertos);

        if (aplicacionTest != null) {
            if (aplicacionTest.getEvaluado() != null) {
                resumen.append("\nEvaluado: ")
                        .append(aplicacionTest.getEvaluado().obtenerNombreCompleto());
            }

            if (aplicacionTest.getEvaluador() != null) {
                resumen.append("\nEvaluador: ")
                        .append(aplicacionTest.getEvaluador().obtenerNombreCompleto());
            }
        }

        if (percentilRT != null) {
            resumen.append("\nNivel general: ").append(interpretarNivel(percentilRT));
        }

        return resumen.toString();
    }

    @PrePersist
    @PreUpdate
    private void validarYCalcularRegistro() {
        if (aplicacionTest == null) {
            throw new IllegalArgumentException("El resultado debe estar asociado a una aplicación.");
        }

        if (r1 != null && r1 < 0) {
            throw new IllegalArgumentException("R1 no puede ser negativo.");
        }

        if (r2 != null && r2 < 0) {
            throw new IllegalArgumentException("R2 no puede ser negativo.");
        }

        calcularAciertos();
        calcularPercentiles();
    }

    private void calcularAciertos() {
        int total = 0;

        if (r1 != null) {
            total += r1;
        }

        if (r2 != null) {
            total += r2;
        }

        aciertos = total;

        if (r1 != null && r2 != null) {
            rt = r1 + r2;
        } else {
            rt = null;
        }
    }

    private void calcularPercentiles() {
        baremoR1 = null;
        baremoR2 = null;
        baremoRT = null;

        percentilR1 = null;
        percentilR2 = null;
        percentilRT = null;

        if (r1 != null) {
            baremoR1 = BaremoRazonamientoService.buscarBaremo(Factor.R1, r1);
            percentilR1 = baremoR1 == null ? null : baremoR1.getPercentil();
        }

        if (r2 != null) {
            baremoR2 = BaremoRazonamientoService.buscarBaremo(Factor.R2, r2);
            percentilR2 = baremoR2 == null ? null : baremoR2.getPercentil();
        }

        if (rt != null) {
            baremoRT = BaremoRazonamientoService.buscarBaremo(Factor.RT, rt);
            percentilRT = baremoRT == null ? null : baremoRT.getPercentil();
        }
    }

    private String valorTexto(Integer numero) {
        return numero == null ? "Sin baremo encontrado" : numero.toString();
    }

    private String interpretarNivel(Integer percentil) {
        if (percentil == null) {
            return "Sin interpretación";
        }

        if (percentil >= 90) {
            return "Muy alto";
        }

        if (percentil >= 75) {
            return "Alto";
        }

        if (percentil >= 50) {
            return "Promedio";
        }

        if (percentil >= 25) {
            return "Bajo";
        }

        return "Muy bajo";
    }
}