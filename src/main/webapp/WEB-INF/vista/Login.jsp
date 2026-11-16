<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión</title>
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<div class="navbar"><div class="contenedor"><span class="marca">Gestor de Gastos</span></div></div>
<div class="contenedor angosto">
    <h2>Iniciar sesión</h2>

    <c:if test="${param.msg == 'registrado'}">
        <div class="alerta alerta-ok">Cuenta creada. Ya puedes iniciar sesión.</div>
    </c:if>
    <c:if test="${param.msg == 'salio'}">
        <div class="alerta alerta-ok">Sesión cerrada.</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alerta alerta-error"><c:out value="${error}"/></div>
    </c:if>

    <form class="formulario" method="post" action="${pageContext.request.contextPath}/LoginServlet">
        <div class="campo">
            <label>Correo</label>
            <input type="email" name="email" maxlength="100" required
                   value="<c:out value='${param.email}'/>">
        </div>
        <div class="campo">
            <label>Contraseña</label>
            <input type="password" name="password" required>
        </div>
        <button type="submit" class="btn btn-primario">Entrar</button>
    </form>
    <p class="enlace">¿No tienes cuenta?
        <a href="${pageContext.request.contextPath}/RegistroServlet">Regístrate</a></p>
</div>
</body>
</html>