# AgroBebederos – App Android (MiApp_GrupoX)

Aplicación móvil para el **control sanitario de bebederos** de Ariztía: permite registrar y consultar temperaturas por granja, galpón y línea, revisar alertas y gestionar registros de flushing.

Proyecto de la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles** (Duoc UC).

## Integrantes

- Solange Valladares – [@solange1988](https://github.com/solange1988)
- José Cardoza – [@Josecardoza12](https://github.com/Josecardoza12)

## Funcionalidades

- **Inicio de sesión único:** el usuario define el perfil (`operario` o `supervisor`).
- **Temperaturas:** registro de mediciones por línea y consulta del estado (normal, advertencia o crítico).
- **Alertas:** listado de líneas en advertencia o crítico, obtenido desde `ms-monitoreo`.
- **Flushing:** crear, editar la observación y eliminar registros contra `ms-flushing`, con evidencia fotográfica (cámara).
- **Panel del supervisor:** contadores por estado y clima actual de Melipilla (API externa Open-Meteo).
- **Modo sin conexión:** copia local con Room; se muestra un aviso cuando no hay conexión con el servidor.
- **Recursos nativos:** cámara, vibración al guardar y permisos en tiempo de ejecución.

## Arquitectura

| Componente | Tecnología | Puerto / Base de datos |
|---|---|---|
| App Android | Kotlin, Jetpack Compose, MVVM, Room, Retrofit | Emulador → `10.0.2.2` |
| `ms-instalaciones` | Java 21, Spring Boot 4.1.2, Maven, MySQL, Flyway | 8081 / `db_instalaciones` |
| `ms-monitoreo` | Java 21, Spring Boot 4.1.2, Maven, MySQL, Flyway | 8082 / `db_monitoreo` |
| `ms-flushing` | Java 21, Spring Boot 4.1.2, Maven, MySQL, Flyway | 8083 / `db_flushing` |
| API externa | Open-Meteo (sin API key) | `api.open-meteo.com` |

Backend: https://github.com/Josecardoza12/Agrobebedero-backend

La app sigue el patrón **MVVM**: la pantalla observa el `StateFlow` del ViewModel y el Repository decide si los datos vienen del microservicio (Retrofit) o de la copia local (Room).

## Endpoints utilizados

### ms-instalaciones (`:8081`)

| Método | Ruta | Descripción |
|---|---|---|
| GET / POST / PUT / DELETE | `/api/lineas` | CRUD de líneas de bebederos (filtros por granja y galpón) |
| GET | `/api/lineas/granjas` | Lista de granjas |
| GET | `/api/lineas/galpones` | Lista de galpones |

### ms-monitoreo (`:8082`)

| Método | Ruta | Descripción |
|---|---|---|
| GET / POST / PUT / DELETE | `/api/mediciones` | CRUD de mediciones de temperatura |
| GET | `/api/mediciones/linea/{lineaId}` | Mediciones de una línea |
| GET | `/api/alertas` | Alertas derivadas de las mediciones |

### ms-flushing (`:8083`)

| Método | Ruta | Descripción |
|---|---|---|
| GET / POST / PUT / DELETE | `/api/flushing` | CRUD de registros de flushing |

### API externa

| Método | Ruta | Descripción |
|---|---|---|
| GET | `https://api.open-meteo.com/v1/forecast` | Temperatura y humedad actual de Melipilla |

Cada microservicio tiene documentación Swagger en `http://localhost:<puerto>/swagger-ui/index.html`.

## Cómo ejecutar

### Requisitos

- Android Studio y un emulador (Android 14 o superior).
- JDK 21.
- Laragon con MySQL (usuario `root`, sin contraseña, puerto 3306).

### Pasos

1. **Iniciar MySQL** desde Laragon.
2. **Crear las bases de datos** (por ejemplo con HeidiSQL): `db_instalaciones`, `db_monitoreo` y `db_flushing`. Las tablas y los datos de ejemplo los crea Flyway al iniciar cada microservicio.
3. **Levantar los 3 microservicios** del repositorio [Agrobebedero-backend](https://github.com/Josecardoza12/Agrobebedero-backend) (cada uno desde IntelliJ o con `./mvnw spring-boot:run`): `ms-instalaciones` (8081), `ms-monitoreo` (8082) y `ms-flushing` (8083).
4. **Abrir este proyecto** en Android Studio y sincronizar Gradle.
5. **Ejecutar la app** (configuración `app`) en el emulador. El emulador accede al equipo con `10.0.2.2`.
6. **Ingresar** con `operario` / `1234` o `supervisor` / `1234`.

## Pruebas

- **Unitarias** (MockK + coroutines-test): `./gradlew testDebugUnitTest`
- **De interfaz** (Compose, emulador encendido): `./gradlew connectedDebugAndroidTest`

## APK firmado

- APK release: _(ruta del archivo en el repositorio)_
- Keystore (`.jks`): _(ruta del archivo en el repositorio)_

### Capturas

| APK firmado | Keystore |
|---|---|
| _(captura)_ | _(captura)_ |

## Estructura del proyecto

```
app/src/main/java/com/example/miapp_grupox/
├── data/remote/   Retrofit: cliente, APIs y DTOs
├── model/         Entidades Room, DAOs y validadores
├── repository/    Repositorios (servidor + Room)
├── viewmodel/     ViewModels (MVVM)
└── ui/theme/      Pantallas y componentes Compose
```