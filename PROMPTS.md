# Bitácora de Prompts (PROMPTS.md)

**Herramienta de IA usada:** Google Gemini (Antigravity Agent)

## Prompt 1: Configuración Inicial del Proyecto y Base de Datos
- **Prompt:** Generar las dependencias de PostgreSQL, JWT y configurar el archivo `application.properties` para un proyecto de Spring Boot con JPA y seguridad.
- **Qué generó la IA:** Archivo `build.gradle` actualizado con las dependencias y un `application.properties` con variables de entorno para la configuración de la BD y JWT.
- **Correcciones/Ajustes:** Ninguna, la IA estructuró correctamente las variables para facilitar su uso con Docker.

## Prompt 2: Entidades y Repositorios
- **Prompt:** Crear las entidades `Branch`, `TableInfo`, `User`, `Order` y `OrderItem` para un sistema de restaurantes, incluyendo relaciones de JPA y roles usando un Enum. Crear sus respectivos repositorios.
- **Qué generó la IA:** Clases de entidad bien estructuradas con anotaciones de JPA, relaciones (OneToMany, ManyToOne) y repositorios básicos.
- **Correcciones/Ajustes:** Tuve que asegurarme de nombrar la entidad de mesas como `TableInfo` porque `table` es una palabra reservada en SQL y Postgres daría un error de sintaxis si se utilizara como nombre de tabla directamente.

## Prompt 3: Seguridad JWT
- **Prompt:** Implementar seguridad basada en JWT en Spring Boot, incluyendo generación de tokens, validación de Access Token y Refresh Token, el filtro `JwtAuthenticationFilter` y la configuración de seguridad `SecurityConfig`.
- **Qué generó la IA:** Un sistema de seguridad completo usando las clases de Spring Security 6, permitiendo el acceso público solo a `/api/auth/**`.
- **Correcciones/Ajustes:** Se verificó que el Refresh Token tenga un tiempo de expiración distinto y mayor al Access Token (15 minutos vs 7 días), como requería el enunciado.

## Prompt 4: Lógica de Negocio (Opción B)
- **Prompt:** Crear `OrderService` donde un Administrador puede ver todo, un Cliente puede ver sus pedidos y un Encargado de turno solo puede acceder y modificar pedidos de su propia sucursal.
- **Qué generó la IA:** Servicio que verifica el rol del usuario autenticado (extraído del contexto de seguridad) y lanza una `AccessDeniedException` si el encargado intenta acceder a una orden de otra sucursal.
- **Correcciones/Ajustes:** Se tuvo que implementar un `SecurityUtils` para extraer de forma limpia al usuario autenticado. La regla de validación de la sucursal fue verificada para garantizar que compare los IDs correctamente.

## Prompt 5: Docker y CI/CD
- **Prompt:** Generar un `Dockerfile` multietapa para compilar y correr la aplicación, un `docker-compose.yml` que incluya PostgreSQL, y un pipeline de GitHub Actions (`ci.yml`) que compile el código y verifique vulnerabilidades (TruffleHog).
- **Qué generó la IA:** Archivos funcionales para contenerizar la aplicación y la base de datos, asegurando la conexión entre ellos a través de variables de entorno, y un pipeline para validación.
- **Correcciones/Ajustes:** Se ajustó la versión de Java a la 21, que es la configurada en el proyecto `build.gradle`.
