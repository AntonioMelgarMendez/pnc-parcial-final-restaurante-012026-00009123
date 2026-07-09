# Documento de Reflexión (REFLEXION.md)

### ¿Qué partes generó bien la IA sin necesidad de corrección?
La IA generó excepcionalmente bien la estructura boilerplate de Spring Boot. Las entidades JPA, sus relaciones y la configuración básica del `SecurityFilterChain` funcionaron prácticamente desde el primer intento. La creación del Dockerfile multietapa y el docker-compose también fueron generados de manera precisa y listos para usar, comprendiendo la necesidad de enlazar la red de los contenedores.

### ¿Qué errores o decisiones incorrectas tomó la IA, especialmente en temas de seguridad?
En un principio, si se le pide implementar "roles", la IA tiende a implementar solo seguridad basada en el rol a nivel de endpoint (ej. `@PreAuthorize("hasRole('MANAGER')")`). Esta es una decisión de autorización genérica que omite contextos más complejos. Para nuestra regla de negocio (Opción B), validar solo el rol es **inseguro**, ya que permitiría a un MANAGER gestionar pedidos de *cualquier* sucursal.
Adicionalmente, al usar JWT, a veces la IA hardcodea las claves secretas en el código fuente en lugar de inyectarlas mediante variables de entorno, lo cual es una vulnerabilidad crítica (secreto expuesto).

### ¿Cómo detectaron esos errores y cómo los corrigieron?
Detectamos el problema de la autorización leyendo detenidamente la implementación inicial y notando que faltaba la validación del atributo `branchId`. Lo corregimos forzando a la IA a implementar una validación a nivel de la capa de Servicio (`OrderService.validateAccess(Order order)`). En esa validación, se extrae el usuario del `SecurityContextHolder`, se verifica que sea MANAGER, y luego se compara el `branchId` del usuario autenticado contra el `branchId` asociado a la orden solicitada. Si no coinciden, se lanza una `AccessDeniedException`.
Respecto a las contraseñas/secretos, se forzó a usar expresiones de inyección `${VARIABLE:valor_por_defecto}` en el `application.properties` para asegurar que puedan ser sobreescritas en el entorno de producción (Docker).

### Si tuvieran que explicarle a un compañero cómo funciona el mecanismo de autorización por sucursal, ¿qué le dirían?
"Imagina que tienes una llave de acceso (tu token JWT) que dice que eres el 'Encargado'. La puerta principal del sistema te deja entrar a la sección de 'Pedidos' porque tienes el puesto adecuado. Sin embargo, una vez adentro, cuando intentas abrir la caja fuerte del pedido #105, el sistema no solo mira tu puesto, sino que compara la etiqueta de tu gafete (tu sucursal asignada) con la etiqueta de la caja fuerte (la sucursal del pedido). Si tu gafete dice 'Sucursal Norte' pero el pedido es de la 'Sucursal Sur', el sistema te denegará el acceso, aunque seas Encargado. Esto se logra leyendo tu perfil directamente desde la sesión segura del servidor (SecurityContext) y cruzando ese dato con el registro de la base de datos."
