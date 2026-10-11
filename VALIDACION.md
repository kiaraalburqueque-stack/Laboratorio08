# Validación de la entrega

Ejecución realizada el 11 de octubre de 2026 con JDK 25 y Maven 3.9.16. El código se compila para Java 17 mediante la configuración de Spring Boot.

| Clase | Pruebas | Fallos | Errores |
|---|---:|---:|---:|
| ProductoServiceTest | 8 | 0 | 0 |
| CategoriaServiceTest | 8 | 0 | 0 |
| ProductoControllerTest | 12 | 0 | 0 |
| CategoriaControllerTest | 12 | 0 | 0 |
| Total | 40 | 0 | 0 |

Resultado Maven: `BUILD SUCCESS`.

Se utilizó un repositorio Maven local dentro de `.tools/repository` porque esta sesión no permite escribir en la caché global. Ese directorio y los logs quedan excluidos de Git.

Para reproducir desde PowerShell:

```powershell
.\mvnw.cmd test
```

La verificación utiliza H2; no se comprobó una instalación MySQL externa. Los XML y reportes de texto detallados se generan en `target/surefire-reports/`.

## Frontend

Se repitieron las 40 pruebas tras añadir la interfaz: cero fallos y cero errores. El JavaScript pasó `node --check`. La comprobación visual interactiva en navegador no está automatizada en este entorno.

Se inició una instancia separada con H2 en memoria en el puerto 8081: `/`, `/app.js`, `/styles.css`, `/api/productos` y `/api/categorias` respondieron HTTP 200. Esta instancia de verificación se detuvo al finalizar.
