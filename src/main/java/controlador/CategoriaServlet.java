package controlador;

import com.mycompany.gestorgastos.Categoria;
import com.mycompany.gestorgastos.sesiones.CategoriaFacade;
import com.mycompany.gestorgastos.sesiones.UsuarioFacade;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CategoriaServlet", urlPatterns = {"/CategoriaServlet"})
public class CategoriaServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/vista/";

    @EJB
    private CategoriaFacade categoriaFacade;
    @EJB
    private UsuarioFacade usuarioFacade;

    private int uid(HttpServletRequest request) {
        return (Integer) request.getSession().getAttribute("idUsuario");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }
        switch (accion) {
            case "nuevo":
                irA(request, response, "AgregarCategoria.jsp");
                break;
            case "editar":
                Categoria c = buscar(request.getParameter("id"), request);
                if (c == null) {
                    response.sendRedirect(request.getContextPath() + "/CategoriaServlet");
                    return;
                }
                request.setAttribute("categoria", c);
                irA(request, response, "EditarCategoria.jsp");
                break;
            default:
                listar(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        String base = request.getContextPath() + "/CategoriaServlet";

        if ("agregar".equals(accion)) {
            Categoria c = new Categoria();
            c.setNombre(limpiar(request.getParameter("nombre")));
            c.setTipo(request.getParameter("tipo"));
            String error = validar(c, request);
            if (error != null) {
                request.setAttribute("error", error);
                request.setAttribute("categoria", c);
                irA(request, response, "AgregarCategoria.jsp");
                return;
            }
            c.setUsuarioIdusuario(usuarioFacade.find(uid(request)));
            categoriaFacade.create(c);
            response.sendRedirect(base + "?msg=agregada");

        } else if ("actualizar".equals(accion)) {
            Categoria c = buscar(request.getParameter("idcategoria"), request);
            if (c == null) {
                response.sendRedirect(base);
                return;
            }
            c.setNombre(limpiar(request.getParameter("nombre")));
            c.setTipo(request.getParameter("tipo"));
            String error = validar(c, request);
            if (error != null) {
                request.setAttribute("error", error);
                request.setAttribute("categoria", c);
                irA(request, response, "EditarCategoria.jsp");
                return;
            }
            categoriaFacade.edit(c);
            response.sendRedirect(base + "?msg=actualizada");

        } else if ("eliminar".equals(accion)) {
            Categoria c = buscar(request.getParameter("idcategoria"), request);
            if (c == null) {
                response.sendRedirect(base);
                return;
            }
            if (categoriaFacade.tieneMovimientos(c.getIdcategoria())) {
                response.sendRedirect(base + "?msg=enuso");
                return;
            }
            categoriaFacade.remove(c);
            response.sendRedirect(base + "?msg=eliminada");

        } else {
            response.sendRedirect(base);
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("categorias", categoriaFacade.findByUsuario(uid(request)));
        String msg = request.getParameter("msg");
        if ("agregada".equals(msg)) {
            request.setAttribute("mensaje", "Categoría agregada correctamente.");
        } else if ("actualizada".equals(msg)) {
            request.setAttribute("mensaje", "Categoría actualizada correctamente.");
        } else if ("eliminada".equals(msg)) {
            request.setAttribute("mensaje", "Categoría eliminada.");
        } else if ("enuso".equals(msg)) {
            request.setAttribute("error", "No se puede eliminar: la categoría tiene movimientos asociados.");
        }
        irA(request, response, "ListarCategorias.jsp");
    }

    private Categoria buscar(String idTexto, HttpServletRequest request) {
        try {
            Categoria c = categoriaFacade.find(Integer.parseInt(idTexto));
            if (c == null || c.getUsuarioIdusuario().getIdusuario() != uid(request)) {
                return null;
            }
            return c;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String limpiar(String s) {
        return s == null ? "" : s.trim();
    }

    private String validar(Categoria c, HttpServletRequest request) {
        if (c.getNombre().isEmpty() || c.getNombre().length() > 50) {
            return "El nombre es obligatorio y debe tener máximo 50 caracteres.";
        }
        if (!"INGRESO".equals(c.getTipo()) && !"GASTO".equals(c.getTipo())) {
            return "Selecciona un tipo válido.";
        }
        if (usuarioFacade.find(uid(request)) == null) {
            return "El usuario de la sesión ya no existe. Vuelve a iniciar sesión.";
        }
        return null;
    }

    private void irA(HttpServletRequest request, HttpServletResponse response, String jsp)
            throws ServletException, IOException {
        request.getRequestDispatcher(VISTA + jsp).forward(request, response);
    }
}