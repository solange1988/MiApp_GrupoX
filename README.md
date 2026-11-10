# MiApp_GrupoX – Control sanitario de bebederos (AgroBebederos)

Aplicación móvil Android (Kotlin + Jetpack Compose) para consultar la temperatura del agua en las líneas de bebederos de una granja avícola, identificar alertas y registrar los resultados de lavado (flushing). La app consume tres microservicios propios y una API externa de clima.

- **Asignatura:** DSY1105 · Desarrollo de Aplicaciones Móviles (Duoc UC)
- **Caso:** Temperatura de agua en bebederos (empresa de servicios avícola y ganadero)
- **Alcance:** MVP académico con datos de ejemplo, sin conexión a sistemas productivos reales

## Integrantes

| Nombre | Usuario de GitHub |
|---|---|
| Solange Valladares | [solange1988](https://github.com/solange1988) |
| José Cardoza | [Josecardoza12](https://github.com/Josecardoza12) |

## Descripción del problema

Los operarios y supervisores controlan la temperatura del agua y ejecutan purga de las líneas cuando detectan condiciones inadecuadas, pero la información no está centralizada en una aplicación móvil. La aplicación permite consultar el estado de las líneas desde el terreno, revisar alertas, registrar mediciones y dejar evidencia de cada descarga.

## Perfiles de usuario

- **Operario:** registra temperaturas, registra flushing con foto de evidencia y revisa alertas.
- **Supervisor:** ve un panel resumen (líneas críticas, en advertencia y normales, y clima de Melipilla) y consulta temperaturas, alertas e historial de flushing.

## Funcionalidades

- **Inicio de sesión único** con validación por campo: el usuario (`operario` o `supervisor`) define el perfil.
- **Temperaturas:** registro por granja, galpón, línea y rango de temperatura; tarjetas por línea con color según estado y botón "Ver detalle".
- **Alertas:** listado de líneas en advertencia o crítico, obtenido desde `ms-monitoreo`.
- **Flushing:** crear, editar la observación y eliminar registros contra `ms-flushing`, con foto de evidencia y estado animado.
- **Panel del supervisor:** contadores por estado y clima actual de Melipilla (API externa Open-Meteo).
- **Modo sin conexión:** copia local con Room y aviso cuando no hay conexión con el servidor.
- **Mensajes** al pie de la pantalla (Snackbar) para confirmaciones y errores.

## Reglas de estado

| Temperatura | Estado |
|---|---|
| Menos de 27 °C | Normal |
| De 27 °C a 29,9 °C | Advertencia |
| 30 °C o más | Crítico |

## Arquitectura

| Componente | Tecnología | Puerto / Base de datos |
|---|---|---|
| App Android | Kotlin, Jetpack Compose, MVVM, Room, Retrofit | Emulador → `10.0.2.2` |
| `ms-instalaciones` | Java 21, Spring Boot 4.1.2, Maven, MySQL, Flyway | 8081 / `db_instalaciones` |
| `ms-monitoreo` | Java 21, Spring Boot 4.1.2, Maven, MySQL, Flyway | 8082 / `db_monitoreo` |
| `ms-flushing` | Java 21, Spring Boot 4.1.2, Maven, MySQL, Flyway | 8083 / `db_flushing` |
| API externa | Open-Meteo (sin API key) | `api.open-meteo.com` |

Repositorio del backend: https://github.com/Josecardoza12/Agrobebedero-backend

La app sigue el patrón **MVVM**: la pantalla observa el `StateFlow` del ViewModel y el Repository decide si los datos vienen del microservicio (Retrofit) o de la copia local (Room).

```
app/src/main/java/com/example/miapp_grupox/
├── data/remote/   Retrofit: cliente, APIs y DTOs
├── model/         Entidades Room, DAOs, validadores y opciones de formularios
├── repository/    Repositorios (servidor + Room)
├── viewmodel/     ViewModels (estado con StateFlow)
└── ui/theme/      Pantallas Compose, componentes reutilizables y tema visual
```

- **Validaciones desacopladas de la interfaz:** `LoginValidator`, `ValidadorTemperatura` y `ValidadorFlushing`. Las pantallas solo muestran los errores que reciben.
- **Persistencia local:** Room guarda las mediciones y los registros de flushing para verlos sin conexión.

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

## Recursos nativos del dispositivo

- **Cámara:** botón para tomar la foto de evidencia en el flushing (`ActivityResultContracts.TakePicturePreview`), con solicitud del permiso `CAMERA` en tiempo de ejecución.
- **Vibración:** confirmación táctil al guardar un registro (permiso `VIBRATE`).

## Animaciones

- `animateColorAsState`: el color de las tarjetas cambia según el estado de la línea.
- `animateDpAsState` y `animateContentSize`: elevación y tamaño de la tarjeta al abrir el detalle.
- `AnimatedVisibility`: detalle de la tarjeta y vista previa de la foto.
- `LaunchedEffect`: simulación del cambio de estado de un lavado (En proceso → Completado).

## Cómo ejecutar el proyecto

### Requisitos

- Android Studio actualizado y un emulador (Android 14 o superior).
- JDK 21.
- Laragon con MySQL (usuario `root`, sin contraseña, puerto 3306).

### Pasos

1. **Iniciar MySQL** desde Laragon.
2. **Crear las bases de datos** (por ejemplo con HeidiSQL): `db_instalaciones`, `db_monitoreo` y `db_flushing`. Las tablas y los datos de ejemplo los crea Flyway al iniciar cada microservicio.
3. **Levantar los 3 microservicios** del repositorio [Agrobebedero-backend](https://github.com/Josecardoza12/Agrobebedero-backend) (cada uno desde IntelliJ o con `./mvnw spring-boot:run`): `ms-instalaciones` (8081), `ms-monitoreo` (8082) y `ms-flushing` (8083).
4. **Clonar este repositorio:** `git clone https://github.com/solange1988/MiApp_GrupoX.git`
5. **Abrir la carpeta** `MiApp_GrupoX` en Android Studio y esperar a que termine la sincronización de Gradle.
6. **Ejecutar la app** (configuración `app`) en el emulador. El emulador accede al equipo con `10.0.2.2`.

### Usuarios de prueba (ficticios)

| Usuario | Contraseña |
|---|---|
| operario | 1234 |
| supervisor | 1234 |

## Pruebas

- **Unitarias** (MockK + coroutines-test): `./gradlew testDebugUnitTest`
- **De interfaz** (Compose, con el emulador encendido): `./gradlew connectedDebugAndroidTest`

## APK firmado

- APK release: _(ruta del archivo en el repositorio)_
- Keystore (`.jks`): _(ruta del archivo en el repositorio)_

### Capturas

| APK firmado | Keystore |
|---|---|
| _(captura)_ | _(captura)_ |

## Organización del trabajo

- **Control de versiones:** GitHub, con ramas `feature/*` y Pull Request a la rama principal, con commits descriptivos por cambio.
- **Planificación:** tablero Trello con las listas Por hacer, En progreso y Hecho: https://trello.com/b/45xADP7N/agrobebederos-backend

## Mejoras futuras

- Notificaciones cuando una línea pasa a estado crítico.
- Guardar la foto de evidencia junto con el registro del historial.
- Inicio de sesión con usuarios y perfiles definidos en el sistema, en lugar de datos de prueba.