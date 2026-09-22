# Todo List App

Aplicación Android de gestión de tareas personales con autenticación de usuarios,
categorías y persistencia local. Desarrollada en Java como trabajo práctico de la
materia **Aplicaciones Móviles**.

## Pantallas

La app cuenta con cinco pantallas (la consigna pedía un mínimo de tres):

| Pantalla | Descripción |
|---|---|
| **Login** | Ingreso con usuario y contraseña. Es el punto de entrada de la app. |
| **Registro** | Alta de nuevos usuarios. |
| **Home** | Listado de tareas del usuario, con filtro por categoría. |
| **Tarea** | Alta y edición de una tarea. |
| **Categorías** | Gestión de las categorías disponibles. |

## Funcionalidades

- Registro e inicio de sesión con sesión persistente entre aperturas de la app.
- Alta, edición, completado y eliminación de tareas.
- Categorización de tareas con color, y filtrado por categoría desde el Home.
- Campos por tarea: título, descripción, fecha de vencimiento, prioridad y estado.
- Cada usuario ve únicamente sus propias tareas.
- Animaciones de transición y feedback visual al completar una tarea.

## Arquitectura

El proyecto separa responsabilidades en tres paquetes:

```
com.example.todolistapp
├── model/    Entidades: Usuario, Tarea, Categoria
├── data/     Repositorios de persistencia y manejo de sesión
├── ui/       Activities, adapters y componentes visuales propios
└── util/     Utilidades (hashing de contraseñas)
```

**Persistencia.** `JsonRepository<T>` es un repositorio genérico sobre
`SharedPreferences` que serializa cada colección a JSON con Gson. Mantiene una
caché en memoria para evitar parsear el JSON en cada lectura, razón por la cual
los repositorios concretos (`UsuarioRepository`, `TareaRepository`,
`CategoriaRepository`) son singleton: dos instancias del mismo repositorio
tendrían cachés independientes que se pisarían entre sí.

**Contraseñas.** No se almacenan en texto plano. `HashUtil` aplica SHA-256 con un
salt aleatorio por usuario, de modo que dos usuarios con la misma contraseña no
comparten hash. Se guarda como `salt:hash` en hexadecimal.

## Stack técnico

| | |
|---|---|
| Lenguaje | Java 11 |
| minSdk / targetSdk | 24 / 37 |
| Gradle | 9.6 |
| Android Gradle Plugin | 9.4.0 |
| UI | Material Components 3, ConstraintLayout, RecyclerView |
| Serialización | Gson 2.11.0 |

## Cómo ejecutarlo

Requiere Android Studio y un dispositivo o emulador con Android 7.0 (API 24) o superior.

```bash
git clone https://github.com/tomasfcalleja/todolistapp-mobile.git
```

Abrir la carpeta desde Android Studio, esperar la sincronización de Gradle y
ejecutar con **Run ▶**.

Para generar el APK instalable:

```bash
./gradlew assembleDebug
```

El archivo queda en `app/build/outputs/apk/debug/app-debug.apk`.

## Autor

Tomás Calleja
