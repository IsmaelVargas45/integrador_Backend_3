<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Crear cuenta</title>
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<div class="navbar"><div class="contenedor"><span class="marca">Gestor de Gastos</span></div></div>
<div class="contenedor angosto">
    <h2>Crear cuenta</h2>

    <c:if test="${not empty error}">
        <div class="alerta alerta-error"><c:out value="${error}"/></div>
    </c:if>

    <form class="formulario" method="post" action="${pageContext.request.contextPath}/RegistroServlet">
        <div class="campo">
            <label>Nombre</label>
            <input type="text" name="nombre" maxlength="100" required
                   value="<c:out value='${param.nombre}'/>">
        </div>
        <div class="campo">
            <label>Correo</label>
            <input type="email" name="email" maxlength="100" required
                   value="<c:out value='${param.email}'/>">
        </div>
        <div class="campo">
            <label>Contraseña (mínimo 6 caracteres)</label>
            <input type="password" name="password" minlength="6" required>
        </div>
        <div class="campo">
            <label>Confirmar contraseña</label>
            <input type="password" name="confirmar" minlength="6" required>
        </div>
        <button type="submit" class="btn btn-primario">Crear cuenta</button>
        <a class="btn btn-secundario" href="${pageContext.request.contextPath}/LoginServlet">Cancelar</a>
    </form>
</div>
</body>
</html>