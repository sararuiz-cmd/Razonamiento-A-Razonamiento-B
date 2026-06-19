package proyecto.com.Razonamiento_A_B.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PortalEvaluadoLogoutServlet", urlPatterns = "/portal/evaluado/logout")
public class PortalEvaluadoLogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PortalSesion.cerrar(request);
        JsonUtil.json(response, JsonUtil.ok("Sesión cerrada"));
    }
}
