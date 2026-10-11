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
