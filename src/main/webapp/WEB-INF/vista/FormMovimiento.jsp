<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${titulo}</title>
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<jsp:include page="botones.jsp"/>
<div class="contenedor angosto">
    <h2>${titulo}</h2>

    <c:if test="${not empty error}">
        <div class="alerta alerta-error"><c:out value="${error}"/></div>
    </c:if>

    <form class="formulario" method="post" action="${pageContext.request.contextPath}/MovimientoServlet">
        <input type="hidden" name="accion" value="${accionForm}">
        <input type="hidden" name="idmovimiento" value="${movimiento.idmovimiento}">

        <div class="campo">
            <label>Descripción</label>
            <input type="text" name="descripcion" maxlength="200"
                   value="<c:out value='${movimiento.descripcion}'/>">
        </div>
        <div class="campo">
            <label>Monto</label>
            <input type="number" name="monto" step="0.01" min="0.01" required
                   value="${movimiento.monto}">
        </div>
        <div class="campo">
            <label>Fecha</label>
            <input type="date" name="fecha" required
                   value="<fmt:formatDate value='${movimiento.fecha}' pattern='yyyy-MM-dd'/>">
        </div>
        <div class="campo">
            <label>Categoría</label>
            <select name="categoria" required>
                <option value="">-- Selecciona --</option>
                <c:forEach var="cat" items="${categorias}">
                    <option value="${cat.idcategoria}"
                        ${cat.idcategoria == movimiento.categoriaIdcategoria.idcategoria ? 'selected' : ''}>
                        <c:out value="${cat.nombre}"/> (${cat.tipo})
                    </option>
                </c:forEach>
            </select>
        </div>

        <button type="submit" class="btn btn-primario">Guardar</button>
        <a class="btn btn-secundario" href="${pageContext.request.contextPath}/MovimientoServlet">Cancelar</a>
    </form>
</div>
</body>
</html>