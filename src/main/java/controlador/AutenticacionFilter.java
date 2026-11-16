package controlador;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AutenticacionFilter", urlPatterns = {"/*"})
public class AutenticacionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        boolean publica = ruta.isEmpty() || ruta.equals("/")
                || ruta.equals("/LoginServlet") || ruta.equals("/RegistroServlet")
                || ruta.startsWith("/css/");

        HttpSession sesion = request.getSession(false);
        boolean logueado = sesion != null && sesion.getAttribute("idUsuario") != null;

        if (publica || logueado) {
            if (!publica) { // evita ver páginas privadas con el botón "atrás" tras salir
                response.setHeader("Cache-Control", "no-store");
            }
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
        }
    }
}