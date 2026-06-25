package proyecto.com.Razonamiento_A_B.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import proyecto.com.Razonamiento_A_B.enums.OpcionRespuesta;
import proyecto.com.Razonamiento_A_B.enums.SubFactor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalificadorRazonamiento {

    private AplicacionTest aplicacionTest;

    public ResultadoRazonamiento calificar() {
        validarAplicacion();

        ResultadoRazonamiento resultado = new ResultadoRazonamiento();
        resultado.setAplicacionTest(aplicacionTest);

        int r1 = calcularR1();
        int r2 = calcularR2();

        resultado.setR1(r1);
        resultado.setR2(r2);
        resultado.setRt(r1 + r2);
        resultado.setAciertos(r1 + r2);

        return resultado;
    }

    public Map<Integer, OpcionRespuesta> recorrerRespuestas() {
        validarAplicacion();

        Map<Integer, OpcionRespuesta> respuestas = new HashMap<>();

        for (RespuestaMarcada respuesta : aplicacionTest.getRespuestasMarcadas()) {
            if (respuesta != null && respuesta.getNumeroItem() != null) {
                respuestas.put(respuesta.getNumeroItem(), respuesta.getRespuestaSeleccionada());
            }
        }

        return respuestas;
    }

    public boolean verificarItem(RespuestaMarcada respuestaMarcada) {
        if (respuestaMarcada == null) {
            return false;
        }

        validarAplicacion();

        ItemRazonamiento item = buscarItem(respuestaMarcada.getNumeroItem());

        return item != null
                && item.verificarRespuesta(respuestaMarcada.getRespuestaSeleccionada());
    }

    public boolean verificarItem() {
        return aplicacionTest != null
                && aplicacionTest.getTestsAplicados() != null
                && !aplicacionTest.getTestsAplicados().isEmpty();
    }

    public int contarAciertos() {
        validarAplicacion();

        int aciertos = 0;

        for (RespuestaMarcada respuesta : aplicacionTest.getRespuestasMarcadas()) {
            if (verificarItem(respuesta)) {
                aciertos++;
            }
        }

        return aciertos;
    }

    public int calcularR1() {
        return contarAciertosPorSubFactor(SubFactor.R1);
    }

    public int calcularR2() {
        return contarAciertosPorSubFactor(SubFactor.R2);
    }

    public int calcularRt() {
        return calcularR1() + calcularR2();
    }

    private int contarAciertosPorSubFactor(SubFactor subFactor) {
        validarAplicacion();

        if (subFactor == null) {
            return 0;
        }

        int total = 0;

        for (RespuestaMarcada respuesta : aplicacionTest.getRespuestasMarcadas()) {
            if (respuesta == null || respuesta.getNumeroItem() == null) {
                continue;
            }

            ItemRazonamiento item = buscarItem(respuesta.getNumeroItem());

            if (item != null
                    && item.getSubFactor() == subFactor
                    && item.verificarRespuesta(respuesta.getRespuestaSeleccionada())) {
                total++;
            }
        }

        return total;
    }

    private ItemRazonamiento buscarItem(Integer numeroItem) {
        if (numeroItem == null) {
            return null;
        }

        validarAplicacion();

        for (TestRazonamiento test : aplicacionTest.getTestsAplicados()) {
            if (test == null || test.getItems() == null) {
                continue;
            }

            for (ItemRazonamiento item : test.getItems()) {
                if (item != null
                        && item.getNumero() != null
                        && item.getNumero().equals(numeroItem)) {
                    return item;
                }
            }
        }

        return null;
    }

    private void validarAplicacion() {
        if (aplicacionTest == null) {
            throw new IllegalStateException("Debe asignar una aplicación de test para calificar");
        }

        List<TestRazonamiento> testsAplicados = aplicacionTest.getTestsAplicados();

        if (testsAplicados == null || testsAplicados.isEmpty()) {
            throw new IllegalStateException("La aplicación debe tener al menos un test asociado");
        }

        if (aplicacionTest.getRespuestasMarcadas() == null) {
            throw new IllegalStateException("La aplicación no tiene lista de respuestas marcadas");
        }
    }
}