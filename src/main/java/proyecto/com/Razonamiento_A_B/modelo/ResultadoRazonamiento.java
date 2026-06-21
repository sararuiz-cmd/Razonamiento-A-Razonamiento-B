package proyecto.com.Razonamiento_A_B.modelo;

import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.*;
import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Getter
@Setter
@Table(name = "resultados_razonamiento")
@View(members = "aplicacionTest; Puntajes { r1; r2; rt; aciertos; }")
@Tab(properties = "idResultado,aplicacionTest.idAplicacion,r1,r2,rt,aciertos")
public class ResultadoRazonamiento {
    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado")
    private Integer idResultado;

    @Required
    @DescriptionsList(descriptionProperties = "idAplicacion")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aplicacion_idaplicacion", nullable = false, unique = true)
    private AplicacionTest aplicacionTest;

    @Column(name = "aciertos", nullable = false)
    private Integer aciertos = 0;

    @Min(0)
    @Column(name = "r1", nullable = false)
    private Integer r1 = 0;

    @Min(0)
    @Column(name = "r2", nullable = false)
    private Integer r2 = 0;

    @Column(name = "rt", nullable = false)
    private Integer rt = 0;

    public String generarResumen(BaremoRazonamiento baremo) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("Resumen del resultado de razonamiento");
        resumen.append("\nR1: ").append(valor(r1));
        resumen.append("\nR2: ").append(valor(r2));
        resumen.append("\nRT: ").append(valor(rt));
        resumen.append("\nAciertos: ").append(valor(aciertos));

        if (aplicacionTest != null) {
            if (aplicacionTest.getEvaluado() != null) {
                resumen.append("\nEvaluado: ").append(aplicacionTest.getEvaluado().obtenerNombreCompleto());
            }
            if (aplicacionTest.getEvaluador() != null) {
                resumen.append("\nEvaluador: ").append(aplicacionTest.getEvaluador().obtenerNombreCompleto());
            }
        }

        if (baremo != null) {
            resumen.append("\nPercentil: ").append(baremo.buscarPercentil());
            resumen.append("\nNivel: ").append(baremo.interpretarNivel());
        }
        return resumen.toString();
    }

    @PrePersist
    @PreUpdate
    private void validarRegistro() {
        if (aplicacionTest == null) {
            throw new IllegalArgumentException("El resultado debe estar asociado a una aplicación");
        }
        if (r1 == null) r1 = 0;
        if (r2 == null) r2 = 0;
        rt = r1 + r2;
        aciertos = rt;
    }

    private int valor(Integer numero) {
        return numero == null ? 0 : numero;
    }
}
