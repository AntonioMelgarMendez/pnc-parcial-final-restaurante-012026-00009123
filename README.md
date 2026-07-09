# Restaurant Ordering System API (Parcial Final - Programación N-Capas)

Esta API ha sido desarrollada como parte de la evaluación del Parcial Final de Programación N-Capas, implementando un sistema de gestión de pedidos para una cadena de restaurantes con múltiples sucursales.

## Arquitectura (N-Capas)
El proyecto respeta la arquitectura en capas recomendada para Spring Boot:
- **Capa de Presentación (Controllers):** Expone los endpoints REST y maneja la deserialización y respuestas HTTP (ej. `AuthController`, `OrderController`).
- **Capa de Lógica de Negocio (Services):** Contiene la lógica del negocio, las reglas de autorización personalizadas y la orquestación de operaciones (ej. `OrderService`, `AuthService`).
- **Capa de Acceso a Datos (Repositories & Entities):** Interfaces JPA que interactúan con PostgreSQL usando Spring Data JPA, y clases de entidad (ej. `UserRepository`, `OrderRepository`, `Order`, `TableInfo`).

## Roles y Regla de Negocio
Se ha implementado seguridad basada en JWT con *Access* y *Refresh* Tokens, soportando los siguientes roles:
- `ADMIN`: Acceso total a nivel global.
- `MANAGER` (Encargado de turno): Solo puede gestionar mesas y pedidos.
- `CUSTOMER` (Cliente): Solo tiene acceso a crear, ver y cancelar sus propios pedidos.

**Regla de Negocio Implementada (Opción B - Autorización por atributo):**
Un `MANAGER` no solo está limitado por su rol general de "encargado", sino que la API cuenta con una validación adicional en el `OrderService`. Dicha lógica garantiza que el Manager **solo pueda interactuar con los pedidos pertenecientes a la sucursal (Branch) que le ha sido asignada**. Si un encargado intenta ver o modificar un pedido de una sucursal distinta a la suya, recibirá un error `403 Access Denied`.

## Cómo levantar el proyecto con Docker

La API está contenerizada e incluye una base de datos PostgreSQL, listos para correr.

1. Asegúrate de tener Docker y Docker Compose instalados.
2. Crea un archivo `.env` basado en el ejemplo proporcionado. En la terminal ejecuta:
   ```bash
   cp .env.example .env
   ```
3. Abre la terminal en la raíz de este proyecto.
4. Ejecuta el siguiente comando para compilar e iniciar los servicios:
   ```bash
   docker-compose up --build
   ```
5. La base de datos estará disponible en `localhost:5432` y la API en `localhost:8080`.
6. Si deseas ejecutarlo en segundo plano, añade el flag `-d`: `docker-compose up -d --build`.

## Integración Continua (CI/CD)
Este proyecto cuenta con un workflow en GitHub Actions que se dispara con cada push a la rama `main`, garantizando que el código compile y pase verificaciones de seguridad usando TruffleHog (para evitar secretos expuestos).
