# IABert — Módulo 2: Gestión Académica

> Microservicio REST para la gestión académica de estudiantes: materias, cortes de evaluación, notas, promedios y simulación de nota objetivo. Componente del ecosistema de microservicios **IABert**.

---

## Tabla de Contenido

- [Descripción General](#descripción-general)
- [Equipo](#equipo)
- [Objetivos](#objetivos)
- [Planteamiento del Problema](#planteamiento-del-problema)
- [Requerimientos](#requerimientos)
- [Arquitectura](#arquitectura)
- [Stack Tecnológico](#stack-tecnológico)
- [Endpoints de la API](#endpoints-de-la-api)
- [Diagramas](#diagramas)
- [Gestión del Proyecto](#gestión-del-proyecto)
- [Pruebas y Calidad](#pruebas-y-calidad)
- [Instalación](#instalación)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Referencias](#referencias)

---

## Descripción General

### Resumen Ejecutivo

**IABert** es una plataforma académica construida como sistema de microservicios para apoyar la gestión universitaria. Este repositorio corresponde al **Módulo 2 — Gestión Académica**, desarrollado por el equipo `dark-code-knights`.

- **Problema a resolver:** Los estudiantes no tienen una herramienta centralizada para registrar y hacer seguimiento de sus notas por materia, visualizar sus promedios en tiempo real ni proyectar qué calificación necesitan en evaluaciones futuras para alcanzar su meta académica.
- **Solución propuesta:** API REST que expone operaciones CRUD de materias, cortes de evaluación y notas, con cálculo automático de promedios y un motor de simulación de nota objetivo.
- **Usuarios objetivo:** Estudiantes universitarios autenticados en la plataforma IABert.
- **Impacto esperado:** Reducir la incertidumbre académica dando al estudiante visibilidad total de su situación en cada materia y permitiéndole tomar decisiones informadas sobre sus evaluaciones pendientes.

### Alcance

#### Incluye
- CRUD completo de materias con estructura de cortes de evaluación (R06, R07)
- Registro, edición y eliminación de notas por actividad dentro de cada corte (R08, R09)
- Recálculo automático de promedios de corte y materia al modificar notas
- Dashboard académico con resumen general del estudiante (R05)
- Simulación de nota objetivo: calcula qué se necesita en los cortes pendientes para alcanzar una meta (R10)
- Formato de respuesta estándar unificado para toda la plataforma IABert

#### No Incluye
- Autenticación y generación de JWT (responsabilidad del Módulo 1 — auth-service)
- Interfaz gráfica de usuario (responsabilidad del equipo de Frontend)
- Comunicación activa con el Módulo 5 — stats-service (pendiente para Fase 8)
- Gestión de usuarios o roles (fuera del alcance de este módulo)

---

## Equipo

**Equipo:** `dark-code-knights`

| Integrante          | Rol                     | Responsabilidades |
|---------------------|-------------------------|-------------------|
| Jose Luis Lancheros | Backend Developer (Ops) |                   |
| Natalia Mahecha     | Lider                   |                   |
| Andres Camilo Vivas | Backend Arquitectura    |                   |
| Carlos Uribe        | Frontend Developer      |                   |

---

## Objetivos

### Objetivo General

Desarrollar un microservicio REST de gestión académica que permita a los estudiantes de la plataforma IABert registrar sus materias, llevar el control de sus notas y proyectar su rendimiento académico a lo largo del semestre.

### Objetivos Específicos

- Implementar un CRUD completo de materias con estructura de cortes de evaluación configurables por el estudiante.
- Garantizar la integridad de los datos académicos mediante reglas de negocio estrictas: rango de notas (0.0–5.0), porcentajes que sumen 100%, bloqueo de estructura cuando hay notas registradas.
- Calcular y actualizar automáticamente los promedios de cada corte y de la materia completa ante cualquier cambio en las notas.
- Exponer un dashboard académico que agregue el estado de todas las materias del estudiante en una sola consulta.
- Implementar un motor de simulación que indique al estudiante qué nota mínima requiere en los cortes pendientes para alcanzar su meta.
- Seguir estrictamente la arquitectura hexagonal (Puertos y Adaptadores) para garantizar mantenibilidad e independencia entre capas.

---

## Planteamiento del Problema

### Contexto

En el entorno universitario, los estudiantes gestionan su información académica de forma dispersa: anotan notas en cuadernos, hojas de cálculo o aplicaciones genéricas que no están integradas con la realidad de su plan de estudios. Esto genera confusión sobre su situación académica real.

### Problema

Los estudiantes carecen de una herramienta digital integrada que les permita:
1. Centralizar el registro de sus notas por materia y por corte evaluativo.
2. Ver en tiempo real cómo evoluciona su promedio ante cada calificación nueva.
3. Proyectar con precisión qué necesitan obtener en las evaluaciones que aún no han presentado.

### Dificultades Actuales

- Pérdida de información al no tener un registro persistente y estructurado de notas.
- Cálculos manuales propensos a errores al ponderarse notas por porcentaje de corte.
- Incertidumbre al no saber si todavía es posible aprobar una materia con las evaluaciones restantes.
- Falta de visión global del rendimiento académico en todas las materias del semestre.

### Solución Propuesta

Un microservicio dedicado a la gestión académica que persiste la información del estudiante en PostgreSQL, aplica las reglas de negocio en la capa de dominio y expone una API REST clara para ser consumida por el frontend de IABert o cualquier otro cliente autorizado.

---

## Requerimientos

### Requerimientos Funcionales

| ID  | Requerimiento               | Descripción                                                                                    | Endpoint principal                                       |
|-----|-----------------------------|------------------------------------------------------------------------------------------------|----------------------------------------------------------|
| R05 | Dashboard académico         | Resumen del estado académico del estudiante con todas sus materias y promedios                 | `GET /api/v1/academic/summary`                           |
| R06 | Gestión de materias         | CRUD completo de materias con nombre, créditos, docente y semestre                             | `POST /api/v1/subjects`                                  |
| R07 | Estructura de evaluación    | Configurar y modificar los cortes de evaluación de una materia (porcentajes que sumen 100%)    | `PUT /api/v1/subjects/{id}/evaluation-structure`         |
| R08 | Registro de notas           | Registrar calificaciones por actividad dentro de un corte, con cálculo automático de promedios | `POST /api/v1/subjects/{id}/cuts/{id}/grades`            |
| R09 | Edición de notas            | Editar y eliminar notas con recálculo automático de promedios de corte y materia               | `PUT/DELETE /api/v1/subjects/{id}/cuts/{id}/grades/{id}` |
| R10 | Simulación de nota objetivo | Calcular qué nota necesita el estudiante en los cortes pendientes para alcanzar su meta        | `POST /api/v1/subjects/{id}/simulate`                    |

### Requerimientos No Funcionales

| ID     | Requerimiento                 | Métrica / Descripción                                                                                    |
|--------|-------------------------------|----------------------------------------------------------------------------------------------------------|
| RNF-01 | Puerto del servicio           | Corre en el puerto `:8002`                                                                               |
| RNF-02 | Formato de respuesta estándar | Toda respuesta sigue el wrapper `ApiResponse<T>` unificado de la plataforma IABert                       |
| RNF-03 | Seguridad                     | Autenticación vía JWT emitido por auth-service (`:8001`). Deshabilitada en Sprint 1, se activa en Fase 7 |
| RNF-04 | Arquitectura                  | Estrictamente hexagonal: ninguna capa de infraestructura contamina el dominio                            |
| RNF-05 | Tiempo de respuesta           | Operaciones simples < 200 ms en condiciones normales de red local                                        |
| RNF-06 | Base de datos                 | PostgreSQL con base de datos propia `academic_db`, no compartida con otros módulos                       |
| RNF-07 | Escalabilidad                 | Diseño stateless; puede escalarse horizontalmente sin cambios de código                                  |
| RNF-08 | Documentación                 | API autodocumentada con SpringDoc OpenAPI accesible en `/swagger-ui.html`                                |

### Reglas de Negocio Críticas

- **Materias:** nombre mínimo 3 caracteres, créditos entre 1 y 10. No se permiten duplicados (mismo nombre + mismo semestre por estudiante).
- **Cortes de evaluación:** la suma de `cutPercentage` debe ser exactamente 100. La estructura **no se puede editar** si ya hay notas registradas.
- **Notas:** rango 0.0–5.0. La suma de porcentajes de actividades dentro de un corte no puede superar 100. Al editar o eliminar una nota, se recalculan los promedios automáticamente.
- **Simulación:** si la nota requerida supera 5.0, `isAchievable: false`. Solo se consideran los cortes sin nota registrada.

---

## Arquitectura

### Patrón: Hexagonal (Puertos y Adaptadores)

El servicio sigue estrictamente la arquitectura hexagonal. El **dominio** es el núcleo independiente; las capas externas se comunican con él únicamente a través de **puertos** (interfaces).

```
entrypoints → application → domain ← infrastructure
```

```
src/main/java/com/aibert/dosw/
│
├── entrypoints/                  # Capa de entrada HTTP
│   ├── rest/
│   │   ├── controller/           # @RestController — recibe requests y delega al use case
│   │   └── mapper/               # Convierte Request DTO → domain model
│   └── advice/                   # @RestControllerAdvice — manejo global de excepciones
│
├── application/                  # Capa de aplicación
│   ├── usecase/                  # Casos de uso — orquestan la lógica de negocio
│   ├── service/                  # Servicios transversales (AverageCalculator)
│   ├── dto/
│   │   ├── request/              # DTOs de entrada
│   │   └── response/             # DTOs de salida
│   └── mapper/                   # MapStruct — DTOs ↔ domain models
│
├── domain/                       # Núcleo de negocio — SIN dependencias externas
│   ├── model/                    # Subject, EvaluationCut, Grade, SimulationResult
│   ├── ports/
│   │   ├── in/                   # Interfaces de entrada (casos de uso)
│   │   └── out/                  # Interfaces de salida (repositorios)
│   └── exceptions/               # Excepciones de dominio propias
│
├── infrastructure/               # Capa de infraestructura
│   └── adapters/
│       ├── adapter/              # Implementaciones de puertos out
│       └── persistence/
│           ├── entity/           # @Entity JPA
│           ├── mapper/           # MapStruct: domain ↔ JPA entity
│           └── repository/       # Interfaces JpaRepository
│
└── config/                       # Configuración de Spring (Security)
```

### Principios de diseño aplicados

- **Inversión de dependencias:** el dominio define los puertos; la infraestructura los implementa.
- **Separación de responsabilidades:** ninguna lógica de negocio en controllers ni en entidades JPA.
- **Mapeo sin código manual:** MapStruct genera todos los mappers en tiempo de compilación.
- **Fallo rápido y explícito:** excepciones de dominio propias capturadas por el `GlobalExceptionHandler`.

---

## Stack Tecnológico

| Área          | Tecnología                  | Versión            | Uso                                      |
|---------------|-----------------------------|--------------------|------------------------------------------|
| Lenguaje      | Java                        | 21                 | Lenguaje principal                       |
| Framework     | Spring Boot                 | 3.4.3              | Base del microservicio                   |
| Persistencia  | Spring Data JPA + Hibernate | (incluido en Boot) | ORM y acceso a PostgreSQL                |
| Base de datos | PostgreSQL                  | 16                 | Almacenamiento relacional                |
| Seguridad     | Spring Security + jjwt      | 0.11.5             | JWT (activo en Fase 7)                   |
| Mapeo         | MapStruct                   | 1.5.5.Final        | Conversión entre capas sin código manual |
| Boilerplate   | Lombok                      | 1.18.32            | `@Builder`, `@Getter`, `@Data`, etc.     |
| Documentación | SpringDoc OpenAPI           | 2.8.5              | Swagger UI automático                    |
| Validación    | Spring Boot Validation      | (incluido en Boot) | Bean Validation con `@Valid`             |
| Contenedores  | Docker + Docker Compose     | —                  | Base de datos local en desarrollo        |

---

## Endpoints de la API

La documentación interactiva completa está disponible en **`http://localhost:8002/swagger-ui.html`** una vez levantado el servicio.

### Materias (R06)

| Método   | Ruta                           | Descripción                                  |
|----------|--------------------------------|----------------------------------------------|
| `POST`   | `/api/v1/subjects`             | Crear nueva materia con cortes de evaluación |
| `GET`    | `/api/v1/subjects`             | Listar todas las materias del estudiante     |
| `GET`    | `/api/v1/subjects/{subjectId}` | Obtener detalle de una materia               |
| `PUT`    | `/api/v1/subjects/{subjectId}` | Actualizar materia                           |
| `DELETE` | `/api/v1/subjects/{subjectId}` | Eliminar materia                             |

### Estructura de Evaluación (R07)

| Método | Ruta                                                | Descripción                                       |
|--------|-----------------------------------------------------|---------------------------------------------------|
| `PUT`  | `/api/v1/subjects/{subjectId}/evaluation-structure` | Configurar o reemplazar los cortes de una materia |
| `GET`  | `/api/v1/subjects/{subjectId}/evaluation-structure` | Consultar la estructura de cortes actual          |

### Notas (R08, R09)

| Método   | Ruta                                                         | Descripción                                  |
|----------|--------------------------------------------------------------|----------------------------------------------|
| `POST`   | `/api/v1/subjects/{subjectId}/cuts/{cutId}/grades`           | Registrar nota en un corte                   |
| `GET`    | `/api/v1/subjects/{subjectId}/cuts/{cutId}/grades`           | Listar notas de un corte                     |
| `PUT`    | `/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}` | Editar una nota                              |
| `DELETE` | `/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}` | Eliminar una nota                            |
| `GET`    | `/api/v1/subjects/{subjectId}/averages`                      | Consultar promedios de una materia por corte |

### Dashboard y Simulación (R05, R10)

| Método | Ruta                                    | Descripción                                |
|--------|-----------------------------------------|--------------------------------------------|
| `GET`  | `/api/v1/academic/summary`              | Dashboard académico general del estudiante |
| `POST` | `/api/v1/subjects/{subjectId}/simulate` | Simular nota objetivo en cortes pendientes |

### Formato de respuesta estándar

```json
// Éxito
{
  "success": true,
  "data": { ... },
  "message": "ok"
}

// Error
{
  "success": false,
  "error": "Mensaje descriptivo del error",
  "code": 422
}
```

---

## Diagramas

### Contexto del sistema
- [Diagrama de contexto](docs/diagramas/contexto.png)

### Casos de Uso
- [Casos de uso — R05 a R10](docs/diagramas/casos-uso.png)

### Diagrama de Clases (Dominio)
- [Clases de dominio](docs/diagramas/clases-dominio.png)

### Diagrama de Componentes
- [Componentes hexagonales](docs/diagramas/componentes.png)

### Entidad-Relación
- [Modelo ER — PostgreSQL](docs/diagramas/er.png)

### Diagramas de Secuencia
- [Registrar nota y recalcular promedio](docs/secuencia/registrar-nota.md)
- [Simular nota objetivo](docs/secuencia/simular-nota.md)
- [Configurar estructura de evaluación](docs/secuencia/configurar-estructura.md)

---

## Gestión del Proyecto

### Metodología

**Scrum** — sprints de duración fija con entregas incrementales y revisión al final de cada fase.

### Sprints

| Sprint            | Rama                                | Objetivo                                                                                            | Estado       |
|-------------------|-------------------------------------|-----------------------------------------------------------------------------------------------------|--------------|
| Sprint 1 — Fase 0 | `feature/project-setup`             | Bootstrap del proyecto: estructura base, `ApiResponse<T>`, `GlobalExceptionHandler`, Docker Compose | ✅ Completado |
| Sprint 1 — Fase 1 | `feature/subject-crud`              | CRUD completo de materias con cortes de evaluación (R06)                                            | ✅ Completado |
| Sprint 1 — Fase 2 | `feature/evaluation-structure`      | Configuración de estructura de evaluación con bloqueo por notas (R07)                               | ✅ Completado |
| Sprint 1 — Fase 3 | `feature/grade-registration`        | Registro de notas con cálculo de promedio de corte (R08)                                            | ✅ Completado |
| Sprint 1 — Fase 4 | `feature/grade-management`          | Edición/eliminación de notas con recálculo automático, consulta de promedios (R09)                  | ✅ Completado |
| Sprint 1 — Fase 5 | `feature/academic-dashboard`        | Dashboard académico agregado por estudiante (R05)                                                   | ✅ Completado |
| Sprint 1 — Fase 6 | `feature/grade-simulation`          | Simulación de nota objetivo en cortes pendientes (R10)                                              | ✅ Completado |
| Sprint 2 — Fase 7 | `feature/jwt-security`              | Activar Spring Security con JWT del auth-service                                                    | 🔜 Pendiente |
| Sprint 2 — Fase 8 | `feature/inter-service-integration` | Integración con stats-service vía OpenFeign                                                         | 🔜 Pendiente |

### Riesgos

| Riesgo                                          | Impacto | Probabilidad | Mitigación                                                                               |
|-------------------------------------------------|---------|--------------|------------------------------------------------------------------------------------------|
| Retraso en entrega de JWT por M1 (auth-service) | Alto    | Media        | Desarrollo con header temporal `X-Student-Id`; Fase 7 solo cambia el punto de extracción |
| Inconsistencia de datos entre microservicios    | Medio   | Baja         | Base de datos propia e independiente por módulo                                          |
| Cambios en el modelo de datos del Gateway       | Medio   | Media        | Contratos de API versionados en `/api/v1/`                                               |
| Deuda técnica por falta de tests unitarios      | Alto    | Media        | Priorizar tests de use cases en Sprint 2                                                 |

---

## Pruebas y Calidad

### Estrategia de pruebas

- **Pruebas manuales de integración:** cada fase fue validada end-to-end en Swagger UI antes de hacer merge a `develop`.
- **Pruebas unitarias:** pendientes.
- **Pruebas de contrato de API:** verificación de formato `ApiResponse<T>` en todos los endpoints.

### Flujos de prueba validados por fase

| Fase                              | Casos probados                                                   | Resultado       |
|-----------------------------------|------------------------------------------------------------------|-----------------|
| Fase 1 — Materias                 | 13 casos (CRUD, duplicados, porcentajes inválidos)               | ✅ Todos pasaron |
| Fase 2 — Estructura de evaluación | 11 casos (bloqueo por notas, validaciones)                       | ✅ Todos pasaron |
| Fase 3 — Registro de notas        | 15 casos (rango, capacidad del corte, promedios)                 | ✅ Todos pasaron |
| Fase 4 — Edición de notas         | 16 casos (edición, eliminación, recálculo)                       | ✅ Todos pasaron |
| Fase 5 — Dashboard académico      | 4 casos (sin materias, con materias, GPA, aislamiento)           | ✅ Todos pasaron |
| Fase 6 — Simulación               | 8 casos (alcanzable, inalcanzable, ya asegurada, sin pendientes) | ✅ Todos pasaron |

### Calidad de código

- Arquitectura hexagonal verificada: cero dependencias de infraestructura en el dominio.
- Manejo exhaustivo de excepciones: 9 tipos de excepciones de dominio cubiertas en el `GlobalExceptionHandler`.
- Sin mapeo manual entre capas: 100% MapStruct.

---

## Instalación

### Requisitos previos

- Java 21 o superior
- Maven 3.9+ (o usar el wrapper incluido `./mvnw`)
- Docker y Docker Compose

### 1. Clonar el repositorio

```bash
git clone https://github.com/dark-code-knights/academic-service.git
cd academic-service
```

### 2. Configurar variables de entorno

Crea el archivo `.env` en la raíz del proyecto:

```env
DB_USER=
DB_PASSWORD=
```

### 3. Levantar la base de datos

```bash
docker compose up -d
```

Esto inicia un contenedor de **PostgreSQL 16** en el puerto `5432` con la base de datos `academic_db`.

### 4. Compilar el proyecto

```bash
./mvnw clean compile
```

### 5. Ejecutar el servicio

```bash
./mvnw spring-boot:run
```

El servicio queda disponible en **`http://localhost:8002`**.

### 6. Explorar la API con Swagger

Abre en el navegador: **`http://localhost:8002/swagger-ui.html`**

---

## Estructura del Proyecto

```
academic-service/
│
├── src/main/java/com/aibert/dosw/
│   ├── AcademicServiceApplication.java
│   │
│   ├── domain/
│   │   ├── model/              # Subject, EvaluationCut, Grade, SimulationResult
│   │   ├── ports/
│   │   │   ├── in/             # 8 interfaces de casos de uso
│   │   │   └── out/            # SubjectRepositoryPort, GradeRepositoryPort
│   │   └── exceptions/         # 8 excepciones de dominio propias
│   │
│   ├── application/
│   │   ├── usecase/            # 9 implementaciones de casos de uso
│   │   ├── service/            # AverageCalculator
│   │   ├── dto/
│   │   │   ├── request/        # 6 DTOs de entrada
│   │   │   └── response/       # 7 DTOs de salida
│   │   └── mapper/             # SubjectMapper, GradeMapper (MapStruct)
│   │
│   ├── infrastructure/
│   │   └── adapters/
│   │       ├── adapter/        # SubjectRepositoryAdapter, GradeRepositoryAdapter
│   │       └── persistence/
│   │           ├── entity/     # SubjectEntity, EvaluationCutEntity, GradeEntity
│   │           ├── mapper/     # SubjectPersistenceMapper, GradePersistenceMapper
│   │           └── repository/ # SubjectJpaRepository, EvaluationCutJpaRepository, GradeJpaRepository
│   │
│   ├── entrypoints/
│   │   ├── ApiResponse.java
│   │   ├── advice/             # GlobalExceptionHandler
│   │   └── rest/
│   │       ├── controller/     # SubjectController, EvaluationStructureController,
│   │       │                   # GradeController, AcademicController, SimulationController
│   │       └── mapper/         # SubjectEntrypointMapper
│   │
│   └── config/
│       └── SecurityConfig.java
│
├── src/main/resources/
│   └── application.yml         # Puerto 8002, datasource, JPA, Swagger paths
│
├── docker-compose.yml          # PostgreSQL 16 para desarrollo local
├── .env                        # Variables de entorno (DB_USER, DB_PASSWORD)
├── pom.xml
└── README.md
```

---

## Referencias

- [Spring Boot 3.4 Reference Documentation](https://docs.spring.io/spring-boot/docs/3.4.3/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [MapStruct 1.5 Reference Guide](https://mapstruct.org/documentation/stable/reference/html/)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [PostgreSQL 16 Documentation](https://www.postgresql.org/docs/16/)
- [jjwt — Java JWT Library](https://github.com/jwtk/jjwt)
- [Arquitectura Hexagonal — Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Lombok Project](https://projectlombok.org/)

---

> **Módulo 2 — Gestión Académica** · Equipo `dark-code-knights` · IABert Platform · 2025
