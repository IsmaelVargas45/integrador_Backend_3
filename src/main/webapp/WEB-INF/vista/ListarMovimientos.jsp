<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Movimientos</title>
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<jsp:include page="botones.jsp"/>
<div class="contenedor">
    <div class="encabezado">
        <h2>Movimientos</h2>
        <a class="btn btn-primario" href="${pageContext.request.contextPath}/MovimientoServlet?accion=nuevo">Agregar movimiento</a>
    </div>

    <c:if test="${not empty mensaje}">
        <div class="alerta alerta-ok"><c:out value="${mensaje}"/></div>
    </c:if>

    <form class="filtro" method="get" action="${pageContext.request.contextPath}/MovimientoServlet">
        <label>Desde:</label>
        <input type="date" name="desde" value="<c:out value='${param.desde}'/>">
        <label>Hasta:</label>
        <input type="date" name="hasta" value="<c:out value='${param.hasta}'/>">
        <label>Categoría:</label>
        <select name="categoria">
            <option value="">Todas</option>
            <c:forEach var="cat" items="${categorias}">
                <option value="${cat.idcategoria}" ${param.categoria == cat.idcategoria ? 'selected' : ''}>
                    <c:out value="${cat.nombre}"/>
                </option>
            </c:forEach>
        </select>
        <button class="btn btn-primario" type="submit">Filtrar</button>
        <a class="btn btn-secundario" href="${pageContext.request.contextPath}/MovimientoServlet">Limpiar</a>
    </form>

    <div class="tarjetas">
        <div class="tarjeta">
            <span>Ingresos</span>
            <strong class="positivo"><fmt:formatNumber value="${totalIngresos}" type="number" minFractionDigits="2"/></strong>
        </div>
        <div class="tarjeta">
            <span>Gastos</span>
            <strong class="negativo"><fmt:formatNumber value="${totalGastos}" type="number" minFractionDigits="2"/></strong>
        </div>
        <div class="tarjeta">
            <span>Balance</span>
            <strong class="${balance >= 0 ? 'positivo' : 'negativo'}"><fmt:formatNumber value="${balance}" type="number" minFractionDigits="2"/></strong>
        </div>
    </div>

    <table>
        <thead>
            <tr><th>Fecha</th><th>Descripción</th><th>Categoría</th><th>Monto</th><th>Acciones</th></tr>
        </thead>
        <tbody>
        <c:forEach var="m" items="${movimientos}">
            <tr>
                <td><fmt:formatDate value="${m.fecha}" pattern="dd/MM/yyyy"/></td>
                <td><c:out value="${m.descripcion}"/></td>
                <td>
                    <span class="etiqueta ${m.categoriaIdcategoria.tipo == 'INGRESO' ? 'etiqueta-ingreso' : 'etiqueta-gasto'}">
                        <c:out value="${m.categoriaIdcategoria.nombre}"/>
                    </span>
                </td>
                <td class="${m.categoriaIdcategoria.tipo == 'INGRESO' ? 'positivo' : 'negativo'}">
                    ${m.categoriaIdcategoria.tipo == 'INGRESO' ? '+' : '-'}<fmt:formatNumber value="${m.monto}" type="number" minFractionDigits="2"/>
                </td>
                <td>
                    <div class="acciones">
                        <a class="btn btn-chico btn-editar"
                           href="${pageContext.request.contextPath}/MovimientoServlet?accion=editar&id=${m.idmovimiento}">Editar</a>
                        <form method="post" action="${pageContext.request.contextPath}/MovimientoServlet"
                              onsubmit="return confirm('¿Eliminar este movimiento?');">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="idmovimiento" value="${m.idmovimiento}">
                            <button class="btn btn-chico btn-eliminar" type="submit">Eliminar</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty movimientos}">
            <tr><td colspan="5" class="vacio">No hay movimientos para mostrar.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
</body>
</html>