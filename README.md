# Gestor de Gastos Personales

Aplicación web para registrar ingresos y gastos, organizarlos por categorías y ver un resumen mensual con gráficos. Cada usuario inicia sesión con su cuenta y solo ve su propia información.

Trabajo Práctico Integrador de **Desarrollo Backend III**, desarrollado con Jakarta EE, GlassFish y MySQL.

## Funcionalidades

- Registro e inicio de sesión de usuarios, con contraseñas cifradas (PBKDF2 con sal).
- Al registrarse se crean categorías por defecto (Comida, Transporte, Hogar, Servicios, Salud, Ocio, Otros gastos, Sueldo y Otros ingresos).
- Gestión de **categorías** (alta, edición y baja) de tipo `INGRESO` o `GASTO`. No se puede eliminar una categoría que tiene movimientos.
- Gestión de **movimientos** (alta, edición y baja) con descripción, monto, fecha y categoría.
- Filtros de movimientos por rango de fechas y por categoría, con totales de ingresos, gastos y balance.
- **Resumen mensual**: totales del mes, gráfico de dona con los gastos por categoría y comparación de ingresos contra gastos. Los gráficos se dibujan con SVG y CSS, sin librerías externas.
- Control de acceso: un filtro redirige al login a quien no haya iniciado sesión.

## Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Plataforma | Jakarta EE (Servlets, EJB, JPA, JSTL) |
| Servidor de aplicaciones | GlassFish |
| Persistencia | JPA con EclipseLink |
| Base de datos | MySQL 8.4 (Connector/J) |
| Vistas | JSP + JSTL y CSS propio |
| Construcción | Maven (empaquetado WAR) |
| IDE | Apache NetBeans |

## Estructura del proyecto

```
GestorGastos
├── src/main
│   ├── java
│   │   ├── com/mycompany/gestorgastos            Entidades JPA: Usuario, Categoria, Movimiento
│   │   ├── com/mycompany/gestorgastos/sesiones   EJB Facade: AbstractFacade y un facade por entidad
│   │   ├── com/mycompany/gestorgastos/resources  Configuración REST generada por NetBeans
│   │   ├── controlador                           Servlets y filtro de autenticación
│   │   └── util                                  Clave.java (cifrado de contraseñas)
│   ├── resources/META-INF/persistence.xml        Unidad de persistencia
│   └── webapp
│       ├── css/styles.css
│       └── WEB-INF
│           ├── vista                             Páginas JSP
│           ├── beans.xml
│           ├── glassfish-web.xml
│           └── web.xml
└── pom.xml
```

**Servlets y filtro** (paquete `controlador`): `LoginServlet`, `RegistroServlet`, `CategoriaServlet`, `MovimientoServlet`, `ResumenServlet` y `AutenticacionFilter`.

**Vistas** (`WEB-INF/vista`): `Login.jsp`, `Registro.jsp`, `ListarCategorias.jsp`, `AgregarCategoria.jsp`, `EditarCategoria.jsp`, `ListarMovimientos.jsp`, `FormMovimiento.jsp`, `Resumen.jsp` y `botones.jsp` (menú compartido).

Las vistas están dentro de `WEB-INF`, por lo que solo se pueden abrir a través de un servlet.

## Modelo de datos

```
usuario (1) ───< categoria (1) ───< movimiento >─── (1) usuario
```

Un usuario tiene muchas categorías y muchos movimientos. Cada movimiento pertenece a una categoría y a un usuario.

## Requisitos previos

- JDK 17 o superior
- Apache NetBeans con soporte para Java Web y Jakarta EE
- GlassFish instalado y agregado a NetBeans
- MySQL 8.x y, opcionalmente, MySQL Workbench
- Conector `mysql-connector-j` (archivo `.jar`)

## Instalación y ejecución

### 1. Crear la base de datos

Ejecuta este script en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS gestor_gastos CHARACTER SET utf8mb4;
USE gestor_gastos;

CREATE TABLE usuario (
    idusuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE categoria (
    idcategoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    tipo ENUM('INGRESO','GASTO') NOT NULL,
    usuario_idusuario INT NOT NULL,
    FOREIGN KEY (usuario_idusuario) REFERENCES usuario(idusuario)
);

CREATE TABLE movimiento (
    idmovimiento INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(200),
    monto DECIMAL(10,2) NOT NULL,
    fecha DATE NOT NULL,
    categoria_idcategoria INT NOT NULL,
    usuario_idusuario INT NOT NULL,
    FOREIGN KEY (categoria_idcategoria) REFERENCES categoria(idcategoria),
    FOREIGN KEY (usuario_idusuario) REFERENCES usuario(idusuario)
);
```

### 2. Instalar el driver de MySQL en GlassFish

Copia `mysql-connector-j-8.x.x.jar` en la carpeta `glassfish/domains/domain1/lib` y reinicia GlassFish.

### 3. Crear el pool de conexiones y el recurso JNDI

En la consola de administración (`http://localhost:4848`), ve a **Resources > JDBC > JDBC Connection Pools > New**:

| Campo | Valor |
|---|---|
| Pool Name | `gestorGastosPool` |
| Resource Type | `javax.sql.DataSource` |
| Datasource Classname | `com.mysql.cj.jdbc.MysqlDataSource` |

> Usa exactamente `com.mysql.cj.jdbc.MysqlDataSource` como *Datasource Classname*. Si dejas `com.mysql.jdbc.jdbc2.optional.MysqlDataSource` (driver antiguo) el *Ping* falla.

En **Additional Properties** del pool agrega:

| Nombre | Valor |
|---|---|
| `serverName` | `localhost` |
| `portNumber` | `3306` |
| `databaseName` | `gestor_gastos` |
| `user` | tu usuario de MySQL (por ejemplo `root`) |
| `password` | tu contraseña de MySQL |
| `useSSL` | `false` |
| `allowPublicKeyRetrieval` | `true` |
| `serverTimezone` | `UTC` |

Pulsa **Ping**: debe mostrar *Ping Succeeded*.

Luego crea el recurso en **JDBC Resources > New**:

- **JNDI Name:** `jdbc/gestorGastos`
- **Pool Name:** `gestorGastosPool`

La unidad de persistencia (`my_persistence_unit`) usa ese nombre JNDI en `persistence.xml`.

### 4. Ejecutar el proyecto

1. Abre el proyecto en NetBeans.
2. Inicia GlassFish desde la pestaña **Services**.
3. Haz clic derecho en el proyecto y elige **Clean and Build** y luego **Run**.
4. Abre en el navegador:

```
http://localhost:8080/GestorGastos/
```

## Modo de uso

1. En la pantalla de inicio pulsa **Regístrate** y crea tu cuenta.
2. Inicia sesión. Ya tendrás categorías por defecto, y puedes crear más en **Categorías**.
3. En **Movimientos**, pulsa **Agregar movimiento** y registra tus ingresos y gastos. Usa los filtros para consultarlos.
4. En **Resumen**, elige el mes para ver los totales y el gráfico de gastos por categoría.
5. Pulsa **Salir** para cerrar la sesión.

## Rutas principales

| Ruta | Descripción |
|---|---|
| `/` y `/LoginServlet` | Inicio de sesión |
| `/RegistroServlet` | Registro de usuarios |
| `/MovimientoServlet` | Lista y gestión de movimientos |
| `/CategoriaServlet` | Lista y gestión de categorías |
| `/ResumenServlet` | Resumen mensual |

## Solución de problemas

| Problema | Posible causa |
|---|---|
| El *Ping* del pool falla con error de clase | Falta el `.jar` del conector en `domain1/lib`, o no se reinició GlassFish |
| `Access denied for user` | Usuario o contraseña incorrectos en las propiedades del pool |
| Error de despliegue: *El contenido no está permitido en el prólogo* | Un archivo XML (`web.xml`, `glassfish-web.xml`) tiene texto antes de `<?xml`. Debe empezar en la primera columna de la primera línea |
| `Data source not found` al desplegar | El recurso JNDI `jdbc/gestorGastos` no existe o está mal escrito en `persistence.xml` |
| Se ven las páginas sin estilos | Recarga con Ctrl + F5 para limpiar la caché del CSS |

## Seguridad

- Las contraseñas se almacenan como hash PBKDF2 con sal aleatoria, nunca en texto plano.
- Cada servlet verifica que la categoría o el movimiento pertenezca al usuario de la sesión.
- Las vistas escapan los datos mostrados para evitar inyección de HTML.
- Los formularios usan el patrón Post/Redirect/Get.
- Las credenciales de la base de datos no están en el código: viven en el pool de conexiones de GlassFish.

## Posibles mejoras

- Presupuestos por categoría con alertas.
- Exportación de movimientos a Excel o PDF.
- Gráfico de evolución mensual.
- Recuperación de contraseña y perfil de usuario.
- Paginación de la lista de movimientos.

