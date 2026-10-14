# MiApp_GrupoX — Control sanitario de bebederos

Aplicación móvil Android (Kotlin + Jetpack Compose) para consultar la temperatura del agua en las líneas de bebederos de una granja avícola, identificar alertas y registrar los flushing realizados.

- **Asignatura:** DSY1105 · Desarrollo de Aplicaciones Móviles
- **Evaluación:** Parcial 2 (encargo en parejas)
- **Caso:** Temperatura de agua en bebederos (empresa de servicios avícola y ganadero)
- **Alcance:** MVP académico con datos ficticios, sin conexión a sistemas productivos reales

## Integrantes

| Nombre | Usuario de GitHub |
|---|---|
| solange valladares | solange1988 |
| ángel villegas | Pitikleiiinn |

**Equipo N°:7

## Descripción del problema

Los operarios y supervisores controlan la temperatura del agua y ejecutan flushing (purga de las líneas) cuando detectan condiciones inadecuadas, pero la información no está centralizada en una aplicación móvil. La app permite consultar el estado de las líneas desde terreno, revisar alertas, registrar mediciones y dejar evidencia de cada flushing.

## Perfiles de usuario

- **Operario:** registra temperaturas, registra flushing con foto de evidencia y revisa alertas.
- **Supervisor:** ve un panel resumen (líneas críticas, en advertencia y normales) y consulta temperaturas, alertas e historial de flushing.

## Funcionalidades implementadas

- **Inicio de sesión** con validación por campo y mensajes visuales.
- **Selección de perfil** (Operario o Supervisor).
- **Temperaturas:** registro por granja, galpón, línea y **rango de temperatura** mediante combo box; tarjetas por línea con color según estado (Normal, Advertencia, Crítico) y botón "Ver detalle".
- **Alertas:** listado de líneas en advertencia o estado crítico, con métricas de resumen.
- **Flushing:** formulario validado con combo box, responsable, motivo, observaciones y **foto de evidencia**; historial de registros con estado animado.
- **Panel del supervisor** con métricas calculadas automáticamente.
- **Mensajes al pie de la pantalla (Snackbar)** para confirmaciones y errores.

### Reglas de estado

| Temperatura | Estado |
|---|---|
| Menos de 27 °C | Normal |
| De 27 °C a 29,9 °C | Advertencia |
| 30 °C o más | Crítico |

## Arquitectura (MVVM)

```
app/src/main/java/com/example/miapp_grupox/
├── model/        Entidades de Room, DAOs, base de datos, validadores y opciones de formularios
├── repository/   Acceso a los datos (BebederoRepository, MainRepository)
├── viewmodel/    BebederoViewModel y MainViewModel (estado con StateFlow)
└── ui/theme/     Pantallas (Compose), componentes reutilizables y tema visual
```

- **Validaciones desacopladas de la interfaz:** `LoginValidator`, `ValidadorTemperatura` y `ValidadorFlushing` (en `model/`). Las pantallas solo muestran los errores que reciben.
- **Persistencia local:** Room guarda las mediciones de temperatura y los registros de flushing; los datos se mantienen al cerrar la app.
- **Estado reactivo:** los ViewModels exponen `StateFlow` y las pantallas se actualizan solas.

## Recursos nativos del dispositivo

1. **Cámara:** botón "Tomar foto de evidencia" en el registro de flushing (`ActivityResultContracts.TakePicturePreview`), con solicitud del permiso `CAMERA` en tiempo de ejecución.
2. **Vibración:** confirmación táctil al guardar una temperatura o un flushing (permiso `VIBRATE`).

## Animaciones

- `animateColorAsState`: el color de las tarjetas cambia según el estado de la línea.
- `animateDpAsState` y `animateContentSize`: elevación y tamaño de la tarjeta al abrir el detalle.
- `AnimatedVisibility`: detalle de la tarjeta y vista previa de la foto.
- `LaunchedEffect`: simulación del cambio de estado de un flushing (En proceso → Completado).

## Cómo ejecutar el proyecto

**Requisitos:** Android Studio actualizado, JDK configurado y un emulador (o celular) con Android 7.0 (API 24) o superior.

1. Clonar el repositorio:
   ```
   git clone https://github.com/solange1988/MiApp_GrupoX.git
   ```
2. Abrir la carpeta `MiApp_GrupoX` en Android Studio (**File → Open**) y esperar a que termine el *Gradle sync*.
3. Crear o seleccionar un emulador en **Device Manager** (por ejemplo Medium Phone).
4. Presionar **Run ▶** (o `Shift + F10`).

**Usuarios de prueba (ficticios):**

| Usuario | Contraseña |
|---|---|
| operario | 1234 |
| supervisor | 1234 |

Después de iniciar sesión se elige el perfil con el que se quiere ingresar.

### Generar el APK

**Build → Generate App Bundles or APKs → Generate APKs**. Para instalar una versión nueva en el celular, desinstalar antes la anterior.

## Organización del trabajo

- **Control de versiones:** GitHub, con commits descriptivos por cambio.
- **Planificación:** tablero Trello con las listas Por hacer, En proceso y Hecho.

## Mejoras futuras

- Notificaciones cuando una línea pase a estado crítico.
- Guardar la foto de evidencia junto con el registro del historial.
- Consumir una API real del sistema de monitoreo de temperatura.
- Inicio de sesión con usuarios y perfiles definidos en el sistema, en lugar de datos de prueba.

## enlace trello
-https://trello.com/b/K0H7qjht/kotlin

