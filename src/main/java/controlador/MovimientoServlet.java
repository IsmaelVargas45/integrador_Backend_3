package controlador;

import com.mycompany.gestorgastos.Categoria;
import com.mycompany.gestorgastos.Movimiento;
import com.mycompany.gestorgastos.sesiones.CategoriaFacade;
import com.mycompany.gestorgastos.sesiones.MovimientoFacade;
import com.mycompany.gestorgastos.sesiones.UsuarioFacade;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@WebServlet(name = "MovimientoServlet", urlPatterns = {"/MovimientoServlet"})
public class MovimientoServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/vista/";

    @EJB
    private MovimientoFacade movimientoFacade;
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
                Movimiento nuevo = new Movimiento();
                nuevo.setFecha(new Date());
                mostrarForm(request, response, nuevo, "agregar", "Agregar movimiento");
                break;
            case "editar":
                Movimiento m = buscar(request.getParameter("id"), request);
                if (m == null) {
                    response.sendRedirect(request.getContextPath() + "/MovimientoServlet");
                    return;
                }
                mostrarForm(request, response, m, "actualizar", "Editar movimiento");
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
        String base = request.getContextPath() + "/MovimientoServlet";

        if ("agregar".equals(accion)) {
            Movimiento m = new Movimiento();
            String error = cargar(m, request);
            if (error != null) {
                request.setAttribute("error", error);
                mostrarForm(request, response, m, "agregar", "Agregar movimiento");
                return;
            }
            m.setUsuarioIdusuario(usuarioFacade.find(uid(request)));
            movimientoFacade.create(m);
            response.sendRedirect(base + "?msg=agregado");

        } else if ("actualizar".equals(accion)) {
            Movimiento m = buscar(request.getParameter("idmovimiento"), request);
            if (m == null) {
                response.sendRedirect(base);
                return;
            }
            String error = cargar(m, request);
            if (error != null) {
                request.setAttribute("error", error);
                mostrarForm(request, response, m, "actualizar", "Editar movimiento");
                return;
            }
            movimientoFacade.edit(m);
            response.sendRedirect(base + "?msg=actualizado");

        } else if ("eliminar".equals(accion)) {
            Movimiento m = buscar(request.getParameter("idmovimiento"), request);
            if (m != null) {
                movimientoFacade.remove(m);
            }
            response.sendRedirect(base + "?msg=eliminado");

        } else {
            response.sendRedirect(base);
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Date desde = parseFecha(request.getParameter("desde"));
        Date hasta = parseFecha(request.getParameter("hasta"));
        Integer idCat = parseEntero(request.getParameter("categoria"));

        List<Movimiento> movimientos = movimientoFacade.filtrar(uid(request), desde, hasta, idCat);

        BigDecimal ingresos = BigDecimal.ZERO;
        BigDecimal gastos = BigDecimal.ZERO;
        for (Movimiento m : movimientos) {
            if ("INGRESO".equals(m.getCategoriaIdcategoria().getTipo())) {
                ingresos = ingresos.add(m.getMonto());
            } else {
                gastos = gastos.add(m.getMonto());
            }
        }

        request.setAttribute("movimientos", movimientos);
        request.setAttribute("categorias", categoriaFacade.findByUsuario(uid(request)));
        request.setAttribute("totalIngresos", ingresos);
        request.setAttribute("totalGastos", gastos);
        request.setAttribute("balance", ingresos.subtract(gastos));

        String msg = request.getParameter("msg");
        if ("agregado".equals(msg)) {
            request.setAttribute("mensaje", "Movimiento agregado correctamente.");
        } else if ("actualizado".equals(msg)) {
            request.setAttribute("mensaje", "Movimiento actualizado correctamente.");
        } else if ("eliminado".equals(msg)) {
            request.setAttribute("mensaje", "Movimiento eliminado.");
        }
        request.getRequestDispatcher(VISTA + "ListarMovimientos.jsp").forward(request, response);
    }

    private void mostrarForm(HttpServletRequest request, HttpServletResponse response,
            Movimiento m, String accionForm, String titulo)
            throws ServletException, IOException {
        request.setAttribute("movimiento", m);
        request.setAttribute("categorias", categoriaFacade.findByUsuario(uid(request)));
        request.setAttribute("accionForm", accionForm);
        request.setAttribute("titulo", titulo);
        request.getRequestDispatcher(VISTA + "FormMovimiento.jsp").forward(request, response);
    }

    /** Llena el movimiento con los datos del formulario. Devuelve un mensaje de error o null. */
    private String cargar(Movimiento m, HttpServletRequest request) {
        String descripcion = request.getParameter("descripcion");
        m.setDescripcion(descripcion == null ? "" : descripcion.trim());
        if (m.getDescripcion().length() > 200) {
            return "La descripción admite máximo 200 caracteres.";
        }

        Date fecha = parseFecha(request.getParameter("fecha"));
        m.setFecha(fecha);
        if (fecha == null) {
            return "La fecha no es válida.";
        }

        try {
            String texto = request.getParameter("monto").trim().replace(',', '.');
            BigDecimal monto = new BigDecimal(texto).setScale(2, RoundingMode.HALF_UP);
            m.setMonto(monto);
            if (monto.signum() <= 0) {
                return "El monto debe ser mayor que cero.";
            }
            if (monto.compareTo(new BigDecimal("99999999.99")) > 0) {
                return "El monto es demasiado grande.";
            }
        } catch (Exception e) {
            return "El monto no es válido.";
        }

        Integer idCat = parseEntero(request.getParameter("categoria"));
        Categoria c = idCat == null ? null : categoriaFacade.find(idCat);
        if (c == null || c.getUsuarioIdusuario().getIdusuario() != uid(request)) {
            return "Selecciona una categoría válida.";
        }
        m.setCategoriaIdcategoria(c);

        if (usuarioFacade.find(uid(request)) == null) {
            return "El usuario de la sesión ya no existe. Vuelve a iniciar sesión.";
        }
        return null;
    }

    private Movimiento buscar(String idTexto, HttpServletRequest request) {
        Integer id = parseEntero(idTexto);
        if (id == null) {
            return null;
        }
        Movimiento m = movimientoFacade.find(id);
        if (m == null || m.getUsuarioIdusuario().getIdusuario() != uid(request)) {
            return null;
        }
        return m;
    }

    private Integer parseEntero(String s) {
        try {
            return Integer.valueOf(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Date parseFecha(String s) {
        try {
            return java.sql.Date.valueOf(LocalDate.parse(s.trim()));
        } catch (Exception e) {
            return null;
        }
    }
}