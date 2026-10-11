# Laboratorio 08 — Implementación de tests

Estudiante: ____________________

Sección: ____________________

Fecha: ____________________

## Desarrollo

Se creó un proyecto Spring Boot con los módulos Productos y Categorías. Cada módulo tiene entidad, DTO, repositorio, servicio y controlador. Las validaciones rechazan entradas incompletas o incorrectas. Un manejador global devuelve 400 para datos inválidos y 404 cuando no existe el recurso.

Se implementaron las pruebas base de Productos, las pruebas unitarias de actualizar y obtener un producto inexistente, y las pruebas de integración PUT y DELETE. El módulo Categoría incluye CRUD y pruebas unitarias e integración. Se incorporaron casos adicionales para actualizar y eliminar recursos inexistentes, campos ausentes y JSON malformado.

## Evidencias que debes adjuntar

1. Estructura del proyecto en el IDE.
2. Resultado de ProductoServiceTest.
3. Resultado de ProductoControllerTest.
4. Resultado de CategoriaServiceTest y CategoriaControllerTest.
5. Resumen general de Maven y ejemplos de respuestas HTTP.

## Cinco observaciones propuestas

1. Las pruebas unitarias del servicio usan un repositorio simulado, por lo que no necesitan una conexión a MySQL.
2. Las pruebas de integración cargan el contexto de Spring y verifican solicitudes HTTP mediante MockMvc sin iniciar un servidor externo.
3. Un nombre vacío o compuesto solo por espacios es rechazado por la validación del DTO.
4. Consultar, actualizar o eliminar un ID inexistente produce una respuesta 404; eliminar un registro existente devuelve 204 sin cuerpo.
5. Las pruebas utilizan H2 en memoria y transacciones que se revierten para mantener los casos independientes.

## Cinco conclusiones propuestas

1. Separar controlador, servicio y repositorio permite comprobar cada responsabilidad con mayor claridad.
2. Mockito permite verificar el comportamiento del servicio y las llamadas esperadas al repositorio.
3. Las pruebas de integración complementan las unitarias porque comprueban validación, serialización y persistencia en conjunto.
4. Probar entradas inválidas y recursos inexistentes ayuda a definir respuestas HTTP consistentes para los clientes de la API.
5. Una base de pruebas aislada permite repetir la suite sin alterar los datos de uso de la aplicación; para producción con MySQL conviene además verificar compatibilidad con ese motor.

Estas observaciones y conclusiones son un borrador: revísalas y añade los problemas que realmente encontraste durante tu ejecución.
