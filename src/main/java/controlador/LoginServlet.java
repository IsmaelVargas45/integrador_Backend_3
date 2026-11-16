package controlador;

import com.mycompany.gestorgastos.Usuario;
import com.mycompany.gestorgastos.sesiones.UsuarioFacade;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import util.Clave;

@WebServlet(name = "LoginServlet", urlPatterns = {"", "/LoginServlet"})
public class LoginServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/vista/Login.jsp";

    @EJB
    private UsuarioFacade usuarioFacade;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String base = request.getContextPath();

        if ("salir".equals(request.getParameter("accion"))) {
            HttpSession s = request.getSession(false);
            if (s != null) {
                s.invalidate();
            }
            response.sendRedirect(base + "/LoginServlet?msg=salio");
            return;
        }

        HttpSession s = request.getSession(false);
        if (s != null && s.getAttribute("idUsuario") != null) {
            response.sendRedirect(base + "/MovimientoServlet");
            return;
        }
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        email = email == null ? "" : email.trim().toLowerCase();
        String clave = request.getParameter("password");
        clave = clave == null ? "" : clave;

        Usuario u = email.isEmpty() ? null : usuarioFacade.findByEmail(email);
        if (u == null || !Clave.verificar(clave, u.getPassword())) {
            request.setAttribute("error", "Correo o contraseña incorrectos.");
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        if (!Clave.esHash(u.getPassword())) { // migra contraseñas antiguas
            u.setPassword(Clave.hashear(clave));
            usuarioFacade.edit(u);
        }

        HttpSession sesion = request.getSession(true);
        request.changeSessionId();
        sesion.setAttribute("idUsuario", u.getIdusuario());
        sesion.setAttribute("nombreUsuario", u.getNombre());
        response.sendRedirect(request.getContextPath() + "/MovimientoServlet");
    }
}