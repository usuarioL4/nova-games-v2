# Nova Games — backend de microservicios

Base educativa para migrar Nova Games HTML/CSS/JS a React + Vite, React-Bootstrap y Axios. Java 17, Spring Boot 3.4.5, Spring Cloud 2024.0.1, Maven. Siete aplicaciones independientes y una librería compartida.

## 1. Arquitectura

| Aplicación | Puerto | Responsabilidad |
|---|---:|---|
| discovery-server | 8761 | Eureka |
| api-gateway | 8080 | Entrada única, CORS, JWT y enrutamiento |
| auth-service | 8081 | Usuarios, registro, login y JWT RS256 |
| catalog-service | 8082 | Juegos, detalle, filtros, categorías/plataformas derivadas y stock |
| commerce-service | 8083 | Carrito, pedidos e historial, coordinación con catálogo |
| community-service | 8084 | Publicar, listar y eliminar comentarios |
| support-service | 8085 | Contacto y bandeja administrativa |

`common-web` es una librería Maven, no un servicio. Contiene ApiError, BusinessException, GlobalExceptionHandler; cada servicio MVC tiene su propio package security. Gateway tiene sus propios manejadores reactivos. Cada aplicación de negocio tiene su propia base de datos; no se comparten entidades ni repositorios. Commerce llama a Catalog mediante RestClient con LoadBalancer y Eureka; no llama al repositorio ajeno.

```
React -> API Gateway -> Auth / Catalog / Commerce / Community / Support
                                 Commerce -> Catalog (reserva de stock)
Todas las aplicaciones cliente -> Eureka
Auth -> claves RSA; los demás validan JWT usando la clave pública JWKS
```

Paquetes por servicio: `controller` (HTTP), `service` (negocio), `repository` (JPA), `entity` (persistencia), `dto` (contrato), `mapper` (transformación), `config` (infraestructura), `security` (auth). Commerce añade `client` para llamadas a Catalog. El código compartido de excepciones se escanea en cada aplicación MVC. Cada servicio de negocio contiene SecurityConfig, JwtService, SecurityAuthenticator y filter/JwtAuthenticationFilter (OncePerRequestFilter). Auth genera tokens RS256; los demás validan con JWKS. JwtService valida y extrae claims; SecurityAuthenticator convierte roles en autoridades y el filtro instala Authentication en SecurityContextHolder. Gateway usa una variante reactiva con AuthenticationWebFilter; no utiliza Servlet. Eureka no requiere JWT.

## 2. Iniciar con Docker Compose (recomendado)

Requisitos: Docker Desktop con contenedores Linux y Docker Compose V2, conexión a Internet para descargar imágenes y dependencias Maven, puertos 8080–8085 y 8761 disponibles. Se recomienda asignar alrededor de 5–6 GB de RAM a Docker para las siete JVM; el primer build también necesita recursos. No es necesario instalar Java ni Maven en Windows para esta modalidad.

Desde PowerShell en la carpeta raíz:

```powershell
docker compose up --build -d
docker compose ps
```

No necesitas `.env` ni ejecutar un script de configuración. Compose trae credenciales de desarrollo fijas: correo `admin@nova.local`, contraseña `NovaAdmin2026!`. La clave interna de Catalog/Commerce también está definida en Compose. Son credenciales públicas de laboratorio, no aptas para producción. Auth genera automáticamente su par RSA y lo conserva en su volumen. Si ya existía un volumen de Auth, conserva el administrador anterior; no se modifica automáticamente su contraseña.

El Dockerfile multietapa compila el reactor Maven completo dentro de una imagen de Maven/Java 17, y cada aplicación obtiene una imagen runtime independiente con su JAR. `common-web` se compila como dependencia, no como contenedor. Compose usa una red bridge, nombres DNS internos y healthchecks; Eureka inicia primero, luego Auth, y Gateway espera a los servicios de negocio. El JWT conserva su clave RSA en el volumen de Auth. Las cinco bases H2 se guardan en cinco volúmenes separados; no hay MySQL ni productos precargados. El perfil MySQL manual permanece disponible (ver sección 3).

```powershell
docker compose logs -f api-gateway
docker compose logs -f auth-service
docker compose restart catalog-service
docker compose down
```

`down` detiene los contenedores y conserva datos. `docker compose down -v` **borra** las cinco bases, el administrador y las claves RSA; úsalo únicamente si quieres reiniciar todo. Cambiar la contraseña en `compose.yaml` no actualiza un administrador ya creado. Para reconstruir después de cambios en Java, ejecuta `docker compose up --build -d`.

Comprobaciones desde Windows (espera a que Eureka registre los servicios):

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
Invoke-RestMethod http://localhost:8080/api/productos
# La segunda llamada debe responder [] en una instalación nueva.
```

Eureka: http://localhost:8761. Gateway: http://localhost:8080. Swagger directo de negocio: http://localhost:8081/swagger-ui/index.html (8082–8085 para los demás). Los puertos publicados se enlazan a 127.0.0.1 para desarrollo local, mientras las llamadas entre contenedores usan DNS interno y Eureka. Si la API devuelve 503 al arrancar, espera el registro y consulta `docker compose logs`. React se ejecuta fuera de Docker con Vite en http://localhost:5173 y apunta a `http://localhost:8080/api`. No se modifica el frontend.

## 3. Persistencia y configuración

Por defecto H2 en archivo, una BD por aplicación (`data/auth-service`, `data/catalog-service`, etc.). Los datos sobreviven al reinicio. El único dato inicial es el administrador configurado; categorías y plataformas se derivan de los productos activos, por lo que al comienzo también están vacías. En el formulario de creación escribe las categorías y plataformas (o usa opciones de UI); no requieren CRUD separado.

Perfil opcional `mysql`: crea cinco bases `nova_auth`, `nova_catalog`, `nova_commerce`, `nova_community`, `nova_support`, y usuarios con permisos apropiados. Activa `SPRING_PROFILES_ACTIVE=mysql` únicamente en las cinco aplicaciones de negocio y define DB_URL, DB_USER y DB_PASSWORD por servicio. No compartas una misma base. El perfil usa Hibernate `ddl-auto=update` para desarrollo; migraciones Flyway y credenciales gestionadas son mejoras de producción. Compose usa H2 por defecto; el perfil MySQL no se ha probado en este entorno.

Variables: ADMIN_EMAIL, ADMIN_PASSWORD (Auth; valores de laboratorio incluidos en Compose); INTERNAL_KEY (Catalog y Commerce; mismo valor incluido en Compose); DATA_DIR (ruta de persistencia/clave RSA); JWT_ISSUER (por defecto `novagames`); JWK_URL (`http://localhost:8081/.well-known/jwks.json`); EUREKA_URL (`http://localhost:8761/eureka/`); FRONTEND_ORIGIN (`http://localhost:5173`). Auth genera un par RSA local y lo conserva en DATA_DIR; protege los archivos privados. JWT expira en una hora; no hay refresh token, revocación ni rotación de claves automática. Usa TLS, gestión de secretos y red privada para producción. Gateway no expone `/internal/**`; el endpoint de reserva exige JWT y clave interna. Los puertos de negocio y Eureka deben quedar privados al desplegar.

## 4. Contrato de API (ruta relativa al Gateway)

| Método | Ruta | Acceso |
|---|---|---|
| POST | /api/auth/register | Público |
| POST | /api/auth/login | Público |
| GET | /api/auth/me | Autenticado |
| GET | /api/admin/usuarios | ADMIN |
| GET | /api/productos?q=&categoria=&plataforma= | Público |
| GET | /api/productos/{id} | Público |
| GET | /api/categorias | Público |
| GET | /api/plataformas | Público |
| POST | /api/productos | ADMIN |
| PUT | /api/productos/{id} | ADMIN |
| DELETE | /api/productos/{id} | ADMIN (borrado lógico) |
| GET | /api/carrito | Autenticado |
| PUT | /api/carrito/items | Autenticado |
| DELETE | /api/carrito/items/{idProducto} | Autenticado |
| POST | /api/pedidos | Autenticado |
| GET | /api/pedidos | Autenticado, propios |
| GET | /api/pedidos/{id} | Autenticado, propio |
| GET | /api/admin/pedidos | ADMIN |
| GET | /api/comunidad | Público |
| POST | /api/comunidad | Autenticado |
| DELETE | /api/comunidad/{id} | Propietario o ADMIN |
| POST | /api/contacto | Público |
| GET | /api/admin/contacto | ADMIN |
| PUT | /api/admin/contacto/{id} | ADMIN |

### Ejemplos de cuerpos JSON

Login: `{"email":"<correo>","password":"<contraseña>"}`. Respuesta: `accessToken`, `tokenType`, `expiresIn`, `user` (id, username, email, cellphone, role). En las llamadas privadas: `Authorization: Bearer <accessToken>`.

Registro: `{"username":"<nombre>","email":"<correo>","password":"<contraseña>","confirmPassword":"<contraseña>","cellphone":"<9 a 15 dígitos>","referenceCode":"","ageConfirmed":true}`. Los dominios aceptados siguen el HTML original (Duoc, profesor.duoc y Gmail). Se refuerza la contraseña a 8 caracteres como mínimo, 72 bytes máximo, y se comprueban duplicados. Ajustar los validadores del frontend original (antes permitía 4 caracteres). El rol no se acepta desde el cliente.

Producto: `{"nombre":"<nombre>","categoria":"<categoría>","precio":0,"stock":0,"imagen":"<ruta o URL>","descripcion":"<descripción>","plataformas":["<plataforma>"]}`. `precio` en CLP entero; `imagen` admite una ruta pública del frontend (por ejemplo `/imagenes/game/...`) o URL; no hay subida de archivos. El GET de detalle devuelve estos campos y el ID. El POST no recibe ID. El PUT reemplaza todos los campos.

Carrito: `{"productoId":1,"cantidad":2}`. PUT fija la cantidad (no suma). Consulta devuelve los datos y precios actuales del catálogo. DELETE elimina por ID de producto. Si un producto fue desactivado, elimina ese ítem antes de volver a consultar.

Pedido: `{"items":[{"productoId":1,"cantidad":2}]}` con encabezado `Idempotency-Key: <UUID>` generado en React (`crypto.randomUUID()`). Guarda la misma clave al reintentar la misma compra. Commerce registra un pedido PENDING antes de llamar a Catalog. Catalog valida y descuenta en una transacción, bloqueando productos y persistiendo la reserva por UUID; devuelve precios oficiales y totales. Commerce guarda el resultado CONFIRMED. Si se pierde la respuesta, reintentar con la misma clave y mismos ítems evita descontar dos veces. No es un sistema de pagos ni una saga de producción: los PENDING requieren reintento del cliente, no se compensan automáticamente ni se reconcilian en background. El carrito no se vacía automáticamente; React puede eliminar ítems después de confirmar el pedido. Nunca confíes en un precio enviado por el navegador.

Comunidad: `{"texto":"<comentario>"}`. Usuario y fecha provienen del JWT/servidor. Contacto: `{"nombre":"<nombre>","email":"<correo>","asunto":"<asunto>","mensaje":"<mensaje>"}`. Administración de contacto: `{"resuelto":true}`. El servicio guarda mensajes, no envía correo ni WhatsApp.

## 5. Errores centralizados

Ejemplo HTTP 409:

```json
{
  "timestamp": "2026-11-15T22:00:00Z",
  "status": 409,
  "code": "INSUFFICIENT_STOCK",
  "message": "No hay suficiente stock para el producto",
  "path": "/api/pedidos",
  "fieldErrors": {}
}
```

Validaciones 400 incluyen `fieldErrors` por campo. 401: credenciales/token inválidos; 403: permisos; 404: recurso inexistente; 409: duplicados/stock; 503: servicio no disponible. Los mensajes de negocio de Catalog se trasladan a Commerce; no se reenvían stack traces ni datos SQL. En React, usar `error.response?.data?.message`, `error.response?.data?.fieldErrors`, `error.request` para ausencia de respuesta y una alerta Bootstrap. CORS/preflight, fallos de conexión o errores del navegador no siempre tienen un cuerpo JSON.

## 6. Plan de migración React (no se modifica el ZIP original)

Referencia HTML revisada: home, about, catalog, login, register, community y contact. React actual: App, Encabezado, Catalogo, TarjetaProducto, LoginRegister y datos locales. Orden sugerido:

1. React-Bootstrap y React Router: Home, About, Catálogo, Detalle `/productos/:id`, Comunidad, Contacto, Login, Registro; crear panel Admin y pedidos.
2. Axios + `.env.development` (`VITE_API_URL=http://localhost:8080/api`), instancia `axios.create`, servicios por módulo y errores comunes.
3. Login, registro, permisos USER/ADMIN e interceptor Authorization. No guardar contraseñas en localStorage; decidir almacenamiento de token y proteger contra XSS.
4. Admin: crear el primer producto, actualizar y eliminar. Reemplazar `src/data/productos.js` por GET; filtros y detalle con useParams, loading y error.
5. Carrito autenticado, checkout, historial, stock y errores 409. Reemplazar o migrar el carrito local (sin enviar precio).
6. Comunidad y contacto. Home/About siguen siendo frontend; mapa de eventos estático del HTML, NovaPoints y enlaces WhatsApp no tienen backend definido en esta versión.

## 7. Verificación

Ejecuta `docker compose up --build -d` o, sin Docker, `mvn clean package` y `scripts/start-local.ps1`. Comprueba en Swagger o Postman: GET `/api/productos` devuelve `[]`, GET `/api/productos/1` devuelve 404, POST producto sin token 401, con USER 403 y con ADMIN 201. Crea el primer producto desde el frontend. Prueba pedidos con stock suficiente, insuficiente, y reintento con la misma clave. Comprueba aislamiento de pedidos/carritos con dos usuarios, 400 con campos inválidos y 503 al detener Catalog.

**Estado de entrega:** código fuente generado, POMs XML revisados y sintaxis Java analizada. El entorno de generación no dispone de Maven ni acceso a Maven Central, por lo que no se pudo compilar ni levantar los siete servicios. No se afirma que las pruebas de integración hayan pasado. El primer arranque local es el punto de validación. Arquitectura educativa, no lista para producción.


**Verificación Docker:** se comprobó la estructura de Compose, sus siete servicios, cinco volúmenes, dependencias y referencias al Dockerfile. No se ejecutó Docker ni Maven en el entorno de entrega, por lo que la compilación de las imágenes y las pruebas de arranque siguen pendientes en Docker Desktop.


## Cambios de seguridad V3 (referencia E-Commerce)

- `security/jwt/JwtService.java`: generación/validación de JWT. Auth firma tokens con **RS256**; los otros servicios verifican firma, vencimiento e issuer mediante la clave pública JWKS de Auth.
- `security/filter/JwtAuthenticationFilter.java`: lee la cabecera `Authorization: Bearer ...` y establece el contexto de seguridad (`OncePerRequestFilter` en servicios Servlet).
- `security/config/SecurityConfig.java`: define política `STATELESS`, rutas públicas, roles y manejadores de errores HTTP 401/403.
- `security/SecurityAuthenticator.java`: convierte claims JWT validados en autoridades `ROLE_USER`/`ROLE_ADMIN`.
- El Gateway usa `AuthenticationWebFilter` (WebFlux), no `OncePerRequestFilter` (Servlet).
- `JwtKeys.java` utiliza los imports específicos `com.nimbusds.jose.jwk.RSAKey`, `java.security.interfaces.RSAPublicKey` y `RSAPrivateKey` para evitar la ambigüedad detectada al compilar Auth.

**Diferencia deliberada respecto del E-Commerce de referencia:** aquel usa `io.jsonwebtoken` (JJWT) con HS256/clave secreta compartida y Spring Boot 4 / Java 21. Nova Games mantiene Spring Boot 3.4.5 / Java 17 / OAuth2 Resource Server y RS256 (clave privada solo en Auth) por compatibilidad con su Gateway y diseño de microservicios. No es seguro copiar las dependencias de Maven 1:1 entre versiones distintas.

El Dockerfile construye cada servicio con `mvn -pl ${SERVICE} -am`, es decir, solo el módulo seleccionado y sus dependencias dentro del mismo proyecto Maven, para evitar compilar los siete módulos repetidamente.

Compilación desde el directorio raíz:

```powershell
docker compose up --build -d
```

Si falla, comparte el **primer `COMPILATION ERROR`** de Maven (la última línea `target ... failed to solve` suele ser solo consecuencia).

**Nota:** el código se verificó de forma estática. No se pudo ejecutar `mvn` ni levantar Docker Compose en este entorno; la compilación y los servicios deberán comprobarse al ejecutar el comando anterior.
