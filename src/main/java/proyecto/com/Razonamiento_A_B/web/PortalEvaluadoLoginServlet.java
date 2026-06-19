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
import proyecto.com.Razonamiento_A_B.modelo.Evaluado;

@WebServlet(name = "PortalEvaluadoLoginServlet", urlPatterns = "/portal/evaluado/login")
public class PortalEvaluadoLoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        if (usuario == null || usuario.trim().isEmpty() || contrasena == null || contrasena.trim().isEmpty()) {
            JsonUtil.json(response, JsonUtil.error("Ingrese usuario y contraseña"));
            return;
        }

        try {
            EntityManager em = XPersistence.getManager();
            List<Evaluado> evaluados = em.createQuery(
                            "select e from Evaluado e where lower(e.usuario) = lower(:usuario) and e.contrasena = :contrasena",
                            Evaluado.class)
                    .setParameter("usuario", usuario.trim())
                    .setParameter("contrasena", contrasena)
                    .setMaxResults(1)
                    .getResultList();

            if (evaluados.isEmpty()) {
                JsonUtil.json(response, JsonUtil.error("Usuario o contraseña incorrectos"));
                return;
            }

            Evaluado evaluado = evaluados.get(0);
            PortalSesion.guardarEvaluado(request, evaluado.getIdEvaluado());
            String body = "{\"ok\":true,\"evaluado\":{\"id\":" + evaluado.getIdEvaluado()
                    + ",\"nombre\":\"" + JsonUtil.escape(evaluado.obtenerNombreCompleto()) + "\"}}";
            JsonUtil.json(response, body);
        } catch (Exception ex) {
            JsonUtil.json(response, JsonUtil.error("No se pudo iniciar sesión: " + ex.getMessage()));
        }
    }
}
