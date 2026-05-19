# DMX
Prueba técnica para DMX

Resumen
--------
Este repositorio contiene el backend de la prueba técnica (Spring Boot, Java, Flyway, PostgreSQL). Este README muestra cómo levantar el proyecto localmente, ejecutar las pruebas (unitarias e integración) y cómo probar los endpoints expuestos.

Requisitos
----------
- Java (JDK 21 / OpenJDK 21)
- Maven
- Docker 
- Docker-compose (para ejecutar las pruebas de integración y la app con Docker Compose)

Rutas importantes
------------------
- Código backend: `backend/src/main/java`
- Tests: `backend/src/test/java`
- `pom.xml`: `backend/pom.xml`
- Flyway migrations: `backend/src/main/resources/db/migration`
- Docker Compose: `docker-compose.yaml`

Cómo levantar la aplicación (rápido)
-----------------------------------
1) Desde la raíz del repo:

```bash
git clone https://github.com/Sanhz/DMX.git
cd dmx
```

2) Levantar con Docker Compose (app + Postgres):

```bash
docker-compose up --build
```

- La API estará disponible por defecto en `http://localhost:8080`.
- Flyway ejecutará las migraciones al arrancar la aplicación (la migración inicial `V1__init.sql` crea la tabla `credit_applications`).

Ejecutar localmente sin Docker (desarrollo)
-------------------------------------------
1) Ir al módulo backend:

```bash
cd backend
```

2) Ejecutar desde la JVM local:

```bash
mvn spring-boot:run
```

Cómo ejecutar las pruebas
-------------------------
- Asegurate de que Docker esté corriendo para las pruebas de integración (Testcontainers).
- Ejecuta las pruebas con `sudo` o Añade tu usuario al grupo `docker` si estás en Linux y tienes problemas de permisos con el socket de Docker.

```bash
sudo usermod -aG docker $USER
newgrp docker
```

- Comprueba que Docker está funcionando:

```bash
docker ps
```

- Ejecutar todos los tests (unitarios + integración):

```bash
cd backend
sudo mvn clean test
```

- Ejecutar solo tests unitarios (rápido):

```bash
cd backend
sudo mvn -Dtest="*ServiceTest" test
```

- Ejecutar solo el test de integración (requiere Docker):

```bash
cd backend
sudo mvn -Dtest=CreditApplicationIntegrationTest test
```

Notas sobre Testcontainers / Docker
-----------------------------------
- Las pruebas de integración usan Testcontainers y arrancan un contenedor PostgreSQL real. Para que funcionen, Docker debe estar instalado y el daemon en ejecución.
- Si Testcontainers no encuentra Docker, en Linux suele ser un problema de permisos del socket `/var/run/docker.sock`. Verifica (como se sugiere arriba), que docker se está ejecutando y que tu usuario tiene permisos para acceder al socket.

Endpoints y pruebas manuales (curl / Postman)
--------------------------------------------
Base URL (cuando la app esté corriendo en local): `http://localhost:8080/api/v1/credit-applications`

1) Crear una solicitud (POST)

```bash
curl -s -X POST http://localhost:8080/api/v1/credit-applications \
  -H 'Content-Type: application/json' \
  -d '{
    "customerName": "Hector Sanchez",
    "customerEmail": "hector@test.com",
    "customerRfc": "SAHH900101ABC",
    "requestedAmount": 150000,
    "currency": "MXN",
    "termMonths": 24,
    "annualInterestRate": 0.12
  }'
```

2) Obtener solicitud por id (GET)

```bash
curl -s http://localhost:8080/api/v1/credit-applications/<id>
```

3) Listar solicitudes con paginación (GET)

```bash
curl -s "http://localhost:8080/api/v1/credit-applications?page=0&size=10"
```

4) Actualizar estado de una solicitud (PATCH)

```bash
curl -s -X PATCH http://localhost:8080/api/v1/credit-applications/<id>/status \
  -H 'Content-Type: application/json' \
  -d '{ "status": "UNDER_REVIEW", "reason": "Documents received" }'
```

Postman
-------
- Hay una colección Postman simplificada en `docs/DMX.postman.json` con las peticiones básicas para los endpoints. Impórtala en Postman o ejecútala con `newman` si lo deseas.

Swagger / OpenAPI
------------------
- El proyecto incluye `springdoc-openapi` en el `pom.xml`. Si la aplicación está corriendo, la UI de Swagger esta disponible en `/swagger-ui/index.html` (`http://localhost:8080/swagger-ui/index.html`).

Cobertura y reportes
---------------------
- Durante la build se genera un informe JaCoCo en `backend/target/site/jacoco/index.html`.

Decisiones y pistas útiles para el reviewer
------------------------------------------
- Arquitectura: el proyecto sigue una estructura hexagonal con paquetes `domain`, `application` e `infrastructure`.
- Migraciones: Flyway se encarga de crear la tabla `credit_applications` (ver `V1__init.sql`).
- Fallo de Frankfurter: el servicio tolera fallos en la API externa (los tests unitarios verifican comportamiento cuando el proveedor de tasas falla — `CreateCreditApplicationServiceTest`).
- Caching: Se implementó un mecanismo de caché para las tasas de cambio obtenidas desde la API externa de Frankfurter con el objetivo de reducir llamadas redundantes, mejorar rendimiento y disminuir la dependencia de disponibilidad del servicio externo.

CI/CD - Pipeline de ejemplo para Azure
-----------------------------------
- En `docs/azure-pipeline.md` encontrarás una plantilla genérica orientada a Azure (Azure DevOps / adaptable a GitHub Actions) que describe un pipeline típico para compilar, analizar, construir la imagen Docker y desplegar el servicio en Azure (ACR + AKS o App Service). 
- La plantilla es intencionalmente genérica.
- El fichero incluye además una breve descripción de cada stage y las variables que deberías configurar.

