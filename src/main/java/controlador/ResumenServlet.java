package controlador;

import com.mycompany.gestorgastos.sesiones.MovimientoFacade;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@WebServlet(name = "ResumenServlet", urlPatterns = {"/ResumenServlet"})
public class ResumenServlet extends HttpServlet {

    private static final String[] COLORES = {
        "#6366f1", "#f59e0b", "#10b981", "#ef4444", "#06b6d4",
        "#a855f7", "#ec4899", "#84cc16", "#f97316", "#64748b"};

    @EJB
    private MovimientoFacade movimientoFacade;

    public static class Segmento {
        private final String nombre;
        private final BigDecimal total;
        private final String porcentaje;
        private final String color;
        private final String dash;
        private final String offset;

        public Segmento(String nombre, BigDecimal total, String porcentaje, String color,
                String dash, String offset) {
            this.nombre = nombre;
            this.total = total;
            this.porcentaje = porcentaje;
            this.color = color;
            this.dash = dash;
            this.offset = offset;
        }

        public String getNombre() { return nombre; }
        public BigDecimal getTotal() { return total; }
        public String getPorcentaje() { return porcentaje; }
        public String getColor() { return color; }
        public String getDash() { return dash; }
        public String getOffset() { return offset; }
    }

    private int uid(HttpServletRequest request) {
        return (Integer) request.getSession().getAttribute("idUsuario");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        YearMonth ym;
        try {
            ym = YearMonth.parse(request.getParameter("mes"));
        } catch (Exception e) {
            ym = YearMonth.now();
        }
        Date desde = java.sql.Date.valueOf(ym.atDay(1));
        Date hasta = java.sql.Date.valueOf(ym.atEndOfMonth());

        BigDecimal totalGastos = movimientoFacade.total(uid(request), "GASTO", desde, hasta);
        BigDecimal totalIngresos = movimientoFacade.total(uid(request), "INGRESO", desde, hasta);
        List<Object[]> filas = movimientoFacade.totalPorCategoria(uid(request), "GASTO", desde, hasta);

        // El círculo SVG tiene circunferencia 100, así que el porcentaje equivale al largo del trazo.
        List<Segmento> segmentos = new ArrayList<>();
        double acumulado = 0;
        for (int i = 0; i < filas.size(); i++) {
            Object[] f = filas.get(i);
            BigDecimal valor = (BigDecimal) f[1];
            double pct = totalGastos.signum() > 0
                    ? valor.doubleValue() * 100.0 / totalGastos.doubleValue() : 0;
            double largo = filas.size() == 1 ? pct : Math.max(pct - 0.8, 0.2); // pequeño espacio entre trozos
            String dash = String.format(Locale.US, "%.2f %.2f", largo, 100 - largo);
            String offset = String.format(Locale.US, "%.2f", 25 - acumulado);
            segmentos.add(new Segmento((String) f[0], valor,
                    String.format(Locale.US, "%.1f", pct), COLORES[i % COLORES.length], dash, offset));
            acumulado += pct;
        }

        double max = Math.max(totalIngresos.doubleValue(), totalGastos.doubleValue());
        String pctIngresos = String.format(Locale.US, "%.1f", max > 0 ? totalIngresos.doubleValue() * 100 / max : 0);
        String pctGastos = String.format(Locale.US, "%.1f", max > 0 ? totalGastos.doubleValue() * 100 / max : 0);

        request.setAttribute("mes", ym.toString());
        request.setAttribute("mesAnterior", ym.minusMonths(1).toString());
        request.setAttribute("mesSiguiente", ym.plusMonths(1).toString());
        request.setAttribute("segmentos", segmentos);
        request.setAttribute("totalGastos", totalGastos);
        request.setAttribute("totalIngresos", totalIngresos);
        request.setAttribute("balance", totalIngresos.subtract(totalGastos));
        request.setAttribute("pctIngresos", pctIngresos);
        request.setAttribute("pctGastos", pctGastos);
        request.getRequestDispatcher("/WEB-INF/vista/Resumen.jsp").forward(request, response);
    }
}