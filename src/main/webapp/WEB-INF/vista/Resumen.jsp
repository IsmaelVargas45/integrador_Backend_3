<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Resumen mensual</title>
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<jsp:include page="botones.jsp"/>
<div class="contenedor">
    <div class="encabezado">
        <h2>Resumen mensual</h2>
        <form class="mes-nav" method="get" action="${pageContext.request.contextPath}/ResumenServlet">
            <a class="btn btn-secundario" href="${pageContext.request.contextPath}/ResumenServlet?mes=${mesAnterior}">&lsaquo;</a>
            <input type="month" name="mes" value="${mes}" style="padding:8px 10px;border:1px solid #d1d5db;border-radius:9px;">
            <button class="btn btn-primario" type="submit">Ver</button>
            <a class="btn btn-secundario" href="${pageContext.request.contextPath}/ResumenServlet?mes=${mesSiguiente}">&rsaquo;</a>
        </form>
    </div>

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

    <div class="panel">
        <h3>Gastos por categoría</h3>
        <c:choose>
            <c:when test="${empty segmentos}">
                <div class="alerta alerta-error" style="margin-bottom:0">No hay gastos registrados en este mes.</div>
            </c:when>
            <c:otherwise>
                <div class="grafica">
                    <div class="dona-caja">
                        <svg class="dona" viewBox="0 0 42 42" role="img" aria-label="Gastos por categoría">
                            <circle cx="21" cy="21" r="15.9155" fill="none" stroke="#eef0f6" stroke-width="5"/>
                            <c:forEach var="s" items="${segmentos}">
                                <circle class="seg" cx="21" cy="21" r="15.9155" fill="none"
                                        stroke="${s.color}" stroke-width="5"
                                        stroke-dasharray="${s.dash}" stroke-dashoffset="${s.offset}">
                                    <title><c:out value="${s.nombre}"/>: <fmt:formatNumber value="${s.total}" type="number" minFractionDigits="2"/> (${s.porcentaje}%)</title>
                                </circle>
                            </c:forEach>
                            <text x="21" y="20.2" class="dona-label">Total gastado</text>
                            <text x="21" y="24.4" class="dona-valor"><fmt:formatNumber value="${totalGastos}" type="number" maxFractionDigits="0"/></text>
                        </svg>
                    </div>
                    <ul class="leyenda">
                        <c:forEach var="s" items="${segmentos}">
                            <li>
                                <div class="fila">
                                    <span class="punto" style="background:${s.color}"></span>
                                    <span><c:out value="${s.nombre}"/></span>
                                    <span class="valor"><fmt:formatNumber value="${s.total}" type="number" minFractionDigits="2"/></span>
                                    <span class="pct">${s.porcentaje}%</span>
                                </div>
                                <div class="barra"><span style="width:${s.porcentaje}%;background:${s.color}"></span></div>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="panel comparacion">
        <h3>Ingresos vs gastos</h3>
        <div class="fila-barra">
            <div class="cabecera"><span>Ingresos</span>
                <strong class="positivo"><fmt:formatNumber value="${totalIngresos}" type="number" minFractionDigits="2"/></strong></div>
            <div class="barra"><span style="width:${pctIngresos}%;background:#10b981"></span></div>
        </div>
        <div class="fila-barra">
            <div class="cabecera"><span>Gastos</span>
                <strong class="negativo"><fmt:formatNumber value="${totalGastos}" type="number" minFractionDigits="2"/></strong></div>
            <div class="barra"><span style="width:${pctGastos}%;background:#ef4444"></span></div>
        </div>
    </div>
</div>
</body>
</html>