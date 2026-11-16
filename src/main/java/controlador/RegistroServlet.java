package controlador;

import com.mycompany.gestorgastos.Categoria;
import com.mycompany.gestorgastos.Usuario;
import com.mycompany.gestorgastos.sesiones.CategoriaFacade;
import com.mycompany.gestorgastos.sesiones.UsuarioFacade;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import util.Clave;

@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
public class RegistroServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/vista/Registro.jsp";

    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private CategoriaFacade categoriaFacade;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String nombre = limpiar(request.getParameter("nombre"));
        String email = limpiar(request.getParameter("email")).toLowerCase();
        String clave = request.getParameter("password") == null ? "" : request.getParameter("password");
        String confirmar = request.getParameter("confirmar") == null ? "" : request.getParameter("confirmar");

        String error = null;
        if (nombre.isEmpty() || nombre.length() > 100) {
            error = "El nombre es obligatorio y admite máximo 100 caracteres.";
        } else if (email.length() > 100 || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            error = "El correo no es válido.";
        } else if (clave.length() < 6 || clave.length() > 72) {
            error = "La contraseña debe tener entre 6 y 72 caracteres.";
        } else if (!clave.equals(confirmar)) {
            error = "Las contraseñas no coinciden.";
        } else if (usuarioFacade.findByEmail(email) != null) {
            error = "Ya existe una cuenta con ese correo.";
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(Clave.hashear(clave));
        usuarioFacade.create(u);

        // categorías por defecto para el usuario nuevo
        Usuario creado = usuarioFacade.findByEmail(email);
        String[][] porDefecto = {
            {"Comida", "GASTO"}, {"Transporte", "GASTO"}, {"Hogar", "GASTO"},
            {"Servicios", "GASTO"}, {"Salud", "GASTO"}, {"Ocio", "GASTO"},
            {"Otros gastos", "GASTO"}, {"Sueldo", "INGRESO"}, {"Otros ingresos", "INGRESO"}
        };
        for (String[] d : porDefecto) {
            Categoria c = new Categoria();
            c.setNombre(d[0]);
            c.setTipo(d[1]);
            c.setUsuarioIdusuario(creado);
            categoriaFacade.create(c);
        }

        response.sendRedirect(request.getContextPath() + "/LoginServlet?msg=registrado");
    }

    private String limpiar(String s) {
        return s == null ? "" : s.trim();
    }
}