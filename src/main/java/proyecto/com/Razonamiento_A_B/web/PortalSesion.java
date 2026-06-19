package proyecto.com.Razonamiento_A_B.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class PortalSesion {

    private static final String EVALUADO_ID = "evaluadoId";

    private PortalSesion() {
    }

    public static void guardarEvaluado(HttpServletRequest request, Integer idEvaluado) {
        request.getSession(true).setAttribute(EVALUADO_ID, idEvaluado);
    }

    public static Integer obtenerEvaluadoId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(EVALUADO_ID);
        return value instanceof Integer ? (Integer) value : null;
    }

    public static void cerrar(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
