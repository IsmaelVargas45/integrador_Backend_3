
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Categorías</title>
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<jsp:include page="botones.jsp"/>

<div class="contenedor">
    <div class="encabezado">
        <h2>Categorías</h2>
        <a class="btn btn-primario" href="${pageContext.request.contextPath}/CategoriaServlet?accion=nuevo">Agregar categoría</a>
    </div>

    <c:if test="${not empty mensaje}">
        <div class="alerta alerta-ok"><c:out value="${mensaje}"/></div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alerta alerta-error"><c:out value="${error}"/></div>
    </c:if>

    <table>
        <thead>
            <tr>
                <th>Nombre</th>
                <th>Tipo</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="c" items="${categorias}">
            <tr>
                <td><c:out value="${c.nombre}"/></td>
                <td>
                    <span class="etiqueta ${c.tipo == 'INGRESO' ? 'etiqueta-ingreso' : 'etiqueta-gasto'}">${c.tipo}</span>
                </td>
                <td>
                    <div class="acciones">
                        <a class="btn btn-chico btn-editar"
                           href="${pageContext.request.contextPath}/CategoriaServlet?accion=editar&id=${c.idcategoria}">Editar</a>

                        <form method="post" action="${pageContext.request.contextPath}/CategoriaServlet"
                              onsubmit="return confirm('¿Eliminar esta categoría?');">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="idcategoria" value="${c.idcategoria}">
                            <button class="btn btn-chico btn-eliminar" type="submit">Eliminar</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>

        <c:if test="${empty categorias}">
            <tr>
                <td colspan="3" class="vacio">No hay categorías registradas.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>

</body>
</html>
```