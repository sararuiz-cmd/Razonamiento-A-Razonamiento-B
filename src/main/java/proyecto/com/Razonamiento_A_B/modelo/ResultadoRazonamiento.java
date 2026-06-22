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
        "Datos resultado { aplicacionTest; } " +
                "Puntajes { puntajesSegunAplicacion; } " +
                "Resumen { resumenFinal; }"
)
@Tab(properties = "idResultado,aplicacionTest.idAplicacion,puntajePrincipal,percentilPrincipal,aciertos")
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
    @Column(name = "r1", nullable = false)
    private Integer r1 = 0;

    @ReadOnly
    @Column(name = "percentil_r1")
    private Integer percentilR1;

    @Min(0)
    @Column(name = "r2", nullable = false)
    private Integer r2 = 0;

    @ReadOnly
    @Column(name = "percentil_r2")
    private Integer percentilR2;

    @ReadOnly
    @Column(name = "rt", nullable = false)
    private Integer rt = 0;

    @ReadOnly
    @Column(name = "percentil_rt")
    private Integer percentilRT;

    @ReadOnly
    @Column(name = "aciertos", nullable = false)
    private Integer aciertos = 0;

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
    public String getPuntajesSegunAplicacion() {
        StringBuilder texto = new StringBuilder();

        if (esResultadoCombinado()) {
            texto.append("Resultado combinado Razonamiento A y B");
            texto.append("\nR1: ").append(valorNumero(r1));
            texto.append("\nPercentil R1: ").append(valorTexto(obtenerPercentilR1()));
            texto.append("\nR2: ").append(valorNumero(r2));
            texto.append("\nPercentil R2: ").append(valorTexto(obtenerPercentilR2()));
            texto.append("\nRT: ").append(valorNumero(rt));
            texto.append("\nPercentil RT: ").append(valorTexto(obtenerPercentilRT()));
            texto.append("\nAciertos: ").append(valorNumero(aciertos));
            return texto.toString();
        }

        if (esFormaA()) {
            texto.append("Resultado Razonamiento A");
            texto.append("\nR1: ").append(valorNumero(r1));
            texto.append("\nPercentil R1: ").append(valorTexto(obtenerPercentilR1()));
            texto.append("\nAciertos: ").append(valorNumero(aciertos));
            return texto.toString();
        }

        if (esFormaB()) {
            texto.append("Resultado Razonamiento B");
            texto.append("\nR2: ").append(valorNumero(r2));
            texto.append("\nPercentil R2: ").append(valorTexto(obtenerPercentilR2()));
            texto.append("\nAciertos: ").append(valorNumero(aciertos));
            return texto.toString();
        }

        return "No hay puntajes registrados.";
    }

    @Transient
    @ReadOnly
    public Integer getPuntajePrincipal() {
        if (esResultadoCombinado()) {
            return valorNumero(rt);
        }

        if (esFormaA()) {
            return valorNumero(r1);
        }

        if (esFormaB()) {
            return valorNumero(r2);
        }

        return 0;
    }

    @Transient
    @ReadOnly
    public Integer getPercentilPrincipal() {
        if (esResultadoCombinado()) {
            return obtenerPercentilRT();
        }

        if (esFormaA()) {
            return obtenerPercentilR1();
        }

        if (esFormaB()) {
            return obtenerPercentilR2();
        }

        return null;
    }

    @Transient
    @ReadOnly
    @Stereotype("MEMO")
    public String getResumenFinal() {
        return generarResumen();
    }

    public String generarResumen() {
        StringBuilder resumen = new StringBuilder();

        resumen.append("Resumen del resultado de razonamiento");

        if (idResultado == null) {
            resumen.append("\nId resultado: Pendiente de guardar");
        } else {
            resumen.append("\nId resultado: ").append(idResultado);
        }

        resumen.append("\n").append(getPuntajesSegunAplicacion());

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

        Integer percentilParaInterpretar = getPercentilPrincipal();

        if (percentilParaInterpretar != null) {
            resumen.append("\nNivel: ").append(interpretarNivel(percentilParaInterpretar));
        }

        return resumen.toString();
    }

    @PrePersist
    @PreUpdate
    private void validarYCalcularRegistro() {
        if (aplicacionTest == null) {
            throw new IllegalArgumentException("El resultado debe estar asociado a una aplicación.");
        }

        if (r1 == null) {
            r1 = 0;
        }

        if (r2 == null) {
            r2 = 0;
        }

        if (rt == null) {
            rt = 0;
        }

        if (aciertos == null) {
            aciertos = 0;
        }

        if (r1 < 0) {
            throw new IllegalArgumentException("R1 no puede ser negativo.");
        }

        if (r2 < 0) {
            throw new IllegalArgumentException("R2 no puede ser negativo.");
        }

        calcularAciertos();
        calcularPercentiles();
    }

    private void calcularAciertos() {
        if (esResultadoCombinado()) {
            rt = valorNumero(r1) + valorNumero(r2);
            aciertos = rt;
            return;
        }

        if (esFormaA()) {
            r2 = 0;
            rt = 0;
            aciertos = valorNumero(r1);
            return;
        }

        if (esFormaB()) {
            r1 = 0;
            rt = 0;
            aciertos = valorNumero(r2);
            return;
        }

        rt = 0;
        aciertos = valorNumero(r1) + valorNumero(r2);
    }

    private void calcularPercentiles() {
        baremoR1 = null;
        baremoR2 = null;
        baremoRT = null;

        percentilR1 = null;
        percentilR2 = null;
        percentilRT = null;

        if (esResultadoCombinado()) {
            baremoR1 = BaremoRazonamientoService.buscarBaremo(Factor.R1, valorNumero(r1));
            percentilR1 = baremoR1 == null ? null : baremoR1.getPercentil();

            baremoR2 = BaremoRazonamientoService.buscarBaremo(Factor.R2, valorNumero(r2));
            percentilR2 = baremoR2 == null ? null : baremoR2.getPercentil();

            baremoRT = BaremoRazonamientoService.buscarBaremo(Factor.RT, valorNumero(rt));
            percentilRT = baremoRT == null ? null : baremoRT.getPercentil();
            return;
        }

        if (esFormaA()) {
            baremoR1 = BaremoRazonamientoService.buscarBaremo(Factor.R1, valorNumero(r1));
            percentilR1 = baremoR1 == null ? null : baremoR1.getPercentil();
            return;
        }

        if (esFormaB()) {
            baremoR2 = BaremoRazonamientoService.buscarBaremo(Factor.R2, valorNumero(r2));
            percentilR2 = baremoR2 == null ? null : baremoR2.getPercentil();
        }
    }

    private boolean esResultadoCombinado() {
        return valorNumero(r1) > 0 && valorNumero(r2) > 0;
    }

    private boolean esFormaA() {
        if (aplicacionTest == null || aplicacionTest.getTest() == null
                || aplicacionTest.getTest().getTipoTest() == null) {
            return valorNumero(r1) > 0 && valorNumero(r2) == 0;
        }

        return "A".equals(aplicacionTest.getTest().getTipoTest().name());
    }

    private boolean esFormaB() {
        if (aplicacionTest == null || aplicacionTest.getTest() == null
                || aplicacionTest.getTest().getTipoTest() == null) {
            return valorNumero(r2) > 0 && valorNumero(r1) == 0;
        }

        return "B".equals(aplicacionTest.getTest().getTipoTest().name());
    }

    private Integer obtenerPercentilR1() {
        if (percentilR1 != null) {
            return percentilR1;
        }

        BaremoRazonamiento baremo = BaremoRazonamientoService.buscarBaremo(Factor.R1, valorNumero(r1));
        return baremo == null ? null : baremo.getPercentil();
    }

    private Integer obtenerPercentilR2() {
        if (percentilR2 != null) {
            return percentilR2;
        }

        BaremoRazonamiento baremo = BaremoRazonamientoService.buscarBaremo(Factor.R2, valorNumero(r2));
        return baremo == null ? null : baremo.getPercentil();
    }

    private Integer obtenerPercentilRT() {
        if (percentilRT != null) {
            return percentilRT;
        }

        BaremoRazonamiento baremo = BaremoRazonamientoService.buscarBaremo(Factor.RT, valorNumero(rt));
        return baremo == null ? null : baremo.getPercentil();
    }

    private int valorNumero(Integer numero) {
        return numero == null ? 0 : numero;
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