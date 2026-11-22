# 🎮 Nova Games — Full Stack

Nova Games es una aplicación web de comercio electrónico de videojuegos, desarrollada con **React y Spring Boot**, utilizando una arquitectura de microservicios, autenticación JWT y comunicación mediante APIs REST.

## 🛠️ Tecnologías

**Frontend:** React, Vite, React-Bootstrap, Axios y React Router.

**Backend:** Java 17, Spring Boot, Spring Security, JWT (RS256), Spring Data JPA, H2 y Swagger.

**Infraestructura:** Docker Compose, API Gateway y Eureka Server.

## 🚀 Funcionalidades

- Registro e inicio de sesión con JWT y roles `USER` y `ADMIN`.
- Catálogo de videojuegos con búsqueda y filtros.
- Visualización del detalle de productos.
- Carrito de compras.
- Comunidad con publicaciones y comentarios.
- Formulario de contacto conectado al backend.

### Panel de administración

- **Productos:** crear, editar, eliminar y consultar videojuegos.
- **Usuarios:** listar y filtrar usuarios registrados.
- **Contacto:** consultar mensajes y gestionar su estado.
- **Estadísticas:** visualizar indicadores de productos, inventario, usuarios y contacto.
- Acceso restringido mediante rol `ADMIN`.

## 🔑 Credenciales de administrador

Credenciales predeterminadas para el entorno local de desarrollo:

| Campo | Valor |
|---|---|
| Correo | `admin@nova.local` |
| Contraseña | `NovaAdmin2026!` |
| Rol | `ADMIN` |

> ⚠️ Estas credenciales son exclusivamente para pruebas locales. No deben utilizarse en producción.

## ▶️ Ejecución

**Backend:**

```bash
cd backend
docker compose up --build -d
```

**Frontend:**

```bash
cd frontend
npm install
npm run dev
```

## 🌐 Direcciones locales

- Frontend: `http://localhost:5173`
- API Gateway: `http://localhost:8080`
- Eureka: `http://localhost:8761`

---

**Proyecto académico Full Stack**, orientado al desarrollo de aplicaciones web con arquitectura de microservicios y separación de responsabilidades.
