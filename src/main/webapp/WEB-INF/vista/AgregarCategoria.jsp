<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Agregar categoría</title>
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<jsp:include page="botones.jsp"/>
<div class="contenedor angosto">
    <h2>Agregar categoría</h2>

    <c:if test="${not empty error}">
        <div class="alerta alerta-error"><c:out value="${error}"/></div>
    </c:if>

    <form class="formulario" method="post" action="${pageContext.request.contextPath}/CategoriaServlet">
        <input type="hidden" name="accion" value="agregar">
        <div class="campo">
            <label>Nombre</label>
            <input type="text" name="nombre" maxlength="50" required
                   value="<c:out value='${categoria.nombre}'/>">
        </div>
        <div class="campo">
            <label>Tipo</label>
            <select name="tipo" required>
                <option value="GASTO" ${categoria.tipo == 'GASTO' ? 'selected' : ''}>Gasto</option>
                <option value="INGRESO" ${categoria.tipo == 'INGRESO' ? 'selected' : ''}>Ingreso</option>
            </select>
        </div>
        <button type="submit" class="btn btn-primario">Guardar</button>
        <a class="btn btn-secundario" href="${pageContext.request.contextPath}/CategoriaServlet">Cancelar</a>
    </form>
</div>
</body>
</html>