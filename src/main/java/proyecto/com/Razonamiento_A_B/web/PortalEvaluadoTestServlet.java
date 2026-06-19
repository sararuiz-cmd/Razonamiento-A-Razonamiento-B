package proyecto.com.Razonamiento_A_B.web;

import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openxava.jpa.XPersistence;
import proyecto.com.Razonamiento_A_B.enums.EstadoAplicacion;
import proyecto.com.Razonamiento_A_B.modelo.AplicacionTest;
import proyecto.com.Razonamiento_A_B.modelo.ItemRazonamiento;
import proyecto.com.Razonamiento_A_B.modelo.TestRazonamiento;

@WebServlet(name = "PortalEvaluadoTestServlet", urlPatterns = "/portal/evaluado/test")
public class PortalEvaluadoTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer evaluadoId = PortalSesion.obtenerEvaluadoId(request);
        if (evaluadoId == null) {
            JsonUtil.json(response, JsonUtil.error("Sesión no iniciada"));
            return;
        }

        try {
            EntityManager em = XPersistence.getManager();
            List<AplicacionTest> aplicaciones = em.createQuery(
                            "select a from AplicacionTest a " +
                                    "join fetch a.evaluado ev " +
                                    "join fetch a.evaluador er " +
                                    "join fetch a.test t " +
                                    "where ev.idEvaluado = :evaluadoId " +
                                    "and (a.estado = :pendiente or a.estado = :enCurso) " +
                                    "order by a.idAplicacion asc",
                            AplicacionTest.class)
                    .setParameter("evaluadoId", evaluadoId)
                    .setParameter("pendiente", EstadoAplicacion.PENDIENTE)
                    .setParameter("enCurso", EstadoAplicacion.EN_CURSO)
                    .setMaxResults(1)
                    .getResultList();

            if (aplicaciones.isEmpty()) {
                JsonUtil.json(response, JsonUtil.error("No tiene una aplicación pendiente o en curso. El evaluador debe asignar el test desde OpenXava."));
                return;
            }

            AplicacionTest aplicacion = aplicaciones.get(0);
            if (aplicacion.getEstado() == EstadoAplicacion.PENDIENTE) {
                aplicacion.iniciarAplicacion();
                XPersistence.commit();
            }

            TestRazonamiento test = aplicacion.getTest();
            List<ItemRazonamiento> items = em.createQuery(
                            "select i from ItemRazonamiento i where i.test.idTest = :testId order by i.numero asc",
                            ItemRazonamiento.class)
                    .setParameter("testId", test.getIdTest())
                    .getResultList();

            StringBuilder json = new StringBuilder();
            json.append("{\"ok\":true");
            json.append(",\"aplicacionId\":").append(aplicacion.getIdAplicacion());
            json.append(",\"evaluado\":\"").append(JsonUtil.escape(aplicacion.getEvaluado().obtenerNombreCompleto())).append("\"");
            json.append(",\"evaluador\":\"").append(JsonUtil.escape(aplicacion.getEvaluador().obtenerNombreCompleto())).append("\"");
            json.append(",\"test\":{\"id\":").append(test.getIdTest());
            json.append(",\"nombre\":\"").append(JsonUtil.escape(test.getNombre())).append("\"");
            json.append(",\"instrucciones\":\"").append(JsonUtil.escape(test.obtenerInstrucciones())).append("\"");
            json.append(",\"tiempoLimite\":").append(test.obtenerTiempoLimite());
            json.append("}");
            json.append(",\"items\":[");
            for (int i = 0; i < items.size(); i++) {
                ItemRazonamiento item = items.get(i);
                if (i > 0) json.append(',');
                json.append("{\"id\":").append(item.getIdItem());
                json.append(",\"numero\":").append(item.getNumero());
                json.append(",\"enunciado\":\"").append(JsonUtil.escape(item.getEnunciado())).append("\"");
                json.append(",\"opciones\":{");
                json.append("\"A\":\"").append(JsonUtil.escape(item.getOpcionA())).append("\",");
                json.append("\"B\":\"").append(JsonUtil.escape(item.getOpcionB())).append("\",");
                json.append("\"C\":\"").append(JsonUtil.escape(item.getOpcionC())).append("\",");
                json.append("\"D\":\"").append(JsonUtil.escape(item.getOpcionD())).append("\"");
                json.append("}}");
            }
            json.append("]}");
            JsonUtil.json(response, json.toString());
        } catch (Exception ex) {
            try { XPersistence.rollback(); } catch (Exception ignored) { }
            JsonUtil.json(response, JsonUtil.error("No se pudo cargar el test: " + ex.getMessage()));
        }
    }
}