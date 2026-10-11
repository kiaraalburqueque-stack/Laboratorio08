# Laboratorio 08: implementación de tests

API REST de Productos y Categorías con Java 17, Spring Boot 3.5.6, JPA, Bean Validation, JUnit 5 y Mockito.

## Ejecutar

Abre esta carpeta como proyecto Maven en IntelliJ IDEA. Selecciona un JDK 17 o superior y espera a que Maven descargue las dependencias.

En PowerShell, con Java instalado (el wrapper descarga Maven automáticamente):

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

También puedes ejecutar `Laboratorio08Application` desde IntelliJ. La API queda en `http://localhost:8080`. H2 guarda los datos de la aplicación en `data/`; las pruebas usan otra base H2 en memoria y revierten cada transacción.

En **Git Bash** utiliza `./mvnw spring-boot:run` y `./mvnw test`.

## Interfaz visual

Abre **http://localhost:8080/** para ver el panel de inventario. Permite cambiar entre Productos y Categorías, buscar por nombre, crear y editar mediante formularios y eliminar con confirmación. El resumen muestra cantidades y el valor del inventario (precio × stock). Los datos se guardan mediante los mismos endpoints REST del laboratorio.

La interfaz es responsive y usa HTML, CSS y JavaScript sin instalar Node ni dependencias de frontend. Incluye estados vacíos y mensajes de validación y errores de conexión. Las fuentes de Google son opcionales y tienen alternativas locales. Si acabas de actualizar el código, reinicia Spring Boot y recarga el navegador para ver los cambios.

## Endpoints

| Método | Productos | Categorías | Resultado |
|---|---|---|---|
| GET | /api/productos | /api/categorias | 200, lista |
| GET | /api/productos/{id} | /api/categorias/{id} | 200 o 404 |
| POST | /api/productos | /api/categorias | 201 y Location |
| PUT | /api/productos/{id} | /api/categorias/{id} | 200 o 404 |
| DELETE | /api/productos/{id} | /api/categorias/{id} | 204 o 404 |

Producto de ejemplo:

```json
{"nombre":"Monitor","precio":500,"stock":10}
```

Categoría de ejemplo:

```json
{"nombre":"Tecnología","descripcion":"Equipos y accesorios"}
```

El nombre es obligatorio y admite hasta 100 caracteres. El precio debe ser mayor que cero y el stock no negativo; ambos son obligatorios. La descripción de categoría es obligatoria y admite hasta 255 caracteres. Datos inválidos devuelven 400 con errores por campo; un ID inexistente devuelve 404 con un mensaje.

Ejemplo en PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/api/productos -Method Post -ContentType 'application/json' -Body '{"nombre":"Monitor","precio":500,"stock":10}'
Invoke-RestMethod http://localhost:8080/api/productos
Invoke-RestMethod http://localhost:8080/api/productos/1 -Method Put -ContentType 'application/json' -Body '{"nombre":"Monitor LED","precio":600,"stock":8}'
Invoke-RestMethod http://localhost:8080/api/productos/1 -Method Delete
```

Sustituye `1` por el ID devuelto al crear el producto.

## Pruebas y evidencias

Hay 8 pruebas unitarias y 12 de integración por entidad (40 en total).

- Las unitarias aíslan el servicio con Mockito: listar, guardar, obtener, actualizar, eliminar y los tres casos de recurso inexistente.
- Las de integración usan `@SpringBootTest`, MockMvc y H2 real: CRUD, persistencia, recursos inexistentes, validaciones, campos ausentes y JSON malformado.
- Para ejecutar una clase: clic derecho sobre `ProductoServiceTest`, `ProductoControllerTest`, `CategoriaServiceTest` o `CategoriaControllerTest`, y selecciona Run.
- Con el wrapper: `.\mvnw.cmd -Dtest=ProductoServiceTest test`. Los resultados se guardan en `target/surefire-reports/`.

Para el informe captura el árbol de paquetes, las cuatro clases de pruebas y el resumen de ejecución en verde. Las observaciones y conclusiones propuestas están en `INFORME.md`; adáptalas a tu experiencia.

## MySQL opcional

La guía original usa MySQL. Este proyecto conserva su conector y añade H2 para ejecutarse sin instalar una base externa. Si necesitas MySQL, crea la base `laboratorio08` y configura:

```powershell
$env:SPRING_PROFILES_ACTIVE='mysql'
$env:DB_USER='root'
$env:DB_PASSWORD='tu_clave'
.\mvnw.cmd spring-boot:run
```

Puedes ajustar `DB_URL`. No subas contraseñas a GitHub. Las pruebas siguen usando H2; ejecútalas sin activar el perfil mysql.

## Estructura

`model` define las entidades; `dto` valida las entradas; `repository` accede a la base; `service` contiene el CRUD; `controller` publica los endpoints; `exception` convierte errores en respuestas HTTP. Las categorías son un módulo independiente: el enunciado no solicita una relación entre productos y categorías.
