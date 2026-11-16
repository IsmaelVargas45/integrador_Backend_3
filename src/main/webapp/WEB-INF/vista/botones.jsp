<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<div class="navbar">
    <div class="contenedor">
        <span class="marca">Gestor de Gastos</span>
        <a href="${pageContext.request.contextPath}/MovimientoServlet">Movimientos</a>
        <a href="${pageContext.request.contextPath}/CategoriaServlet">Categorías</a>
        <a href="${pageContext.request.contextPath}/ResumenServlet">Resumen</a>
        <span class="usuario">
            <c:out value="${sessionScope.nombreUsuario}"/>
            <a href="${pageContext.request.contextPath}/LoginServlet?accion=salir">Salir</a>
        </span>
    </div>
</div>