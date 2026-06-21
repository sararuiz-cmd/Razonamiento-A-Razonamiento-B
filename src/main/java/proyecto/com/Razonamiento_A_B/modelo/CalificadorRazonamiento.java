package proyecto.com.Razonamiento_A_B.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proyecto.com.Razonamiento_A_B.enums.OpcionRespuesta;
import proyecto.com.Razonamiento_A_B.enums.SubFactor;
import java.util.HashMap;
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
        resultado.setR1(calcularR1());
        resultado.setR2(calcularR2());
        resultado.setRt(calcularRt());
        resultado.setAciertos(contarAciertos());
        return resultado;
    }

    public Map<Integer, OpcionRespuesta> recorrerRespuestas() {
        validarAplicacion();
        Map<Integer, OpcionRespuesta> respuestas = new HashMap<>();
        for (RespuestaMarcada respuesta : aplicacionTest.getRespuestasMarcadas()) {
            respuestas.put(respuesta.getNumeroItem(), respuesta.getRespuestaSeleccionada());
        }
        return respuestas;
    }

    public boolean verificarItem(RespuestaMarcada respuestaMarcada) {
        if (respuestaMarcada == null || aplicacionTest == null || aplicacionTest.getTest() == null) {
            return false;
        }
        ItemRazonamiento item = buscarItem(respuestaMarcada.getNumeroItem());
        return item != null && item.verificarRespuesta(respuestaMarcada.getRespuestaSeleccionada());
    }

    public boolean verificarItem() {
        return aplicacionTest != null;
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
        int total = 0;
        for (RespuestaMarcada respuesta : aplicacionTest.getRespuestasMarcadas()) {
            ItemRazonamiento item = buscarItem(respuesta.getNumeroItem());
            if (item != null && item.getSubFactor() == subFactor && item.verificarRespuesta(respuesta.getRespuestaSeleccionada())) {
                total++;
            }
        }
        return total;
    }

    private ItemRazonamiento buscarItem(Integer numeroItem) {
        if (numeroItem == null || aplicacionTest == null || aplicacionTest.getTest() == null) {
            return null;
        }
        for (ItemRazonamiento item : aplicacionTest.getTest().getItems()) {
            if (item.getNumero() != null && item.getNumero().equals(numeroItem)) {
                return item;
            }
        }
        return null;
    }

    private void validarAplicacion() {
        if (aplicacionTest == null) {
            throw new IllegalStateException("Debe asignar una aplicación de test para calificar");
        }
        if (aplicacionTest.getTest() == null) {
            throw new IllegalStateException("La aplicación debe tener un test asociado");
        }
    }
}
