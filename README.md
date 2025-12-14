# Toggle PVP Mod para Minecraft 1.19.2 Forge

Un mod **server-side** que permite a los jugadores activar y desactivar el PvP con comandos simples.

## Características

- 🎮 **Comando `/pvp on`** - Activa el PvP para el jugador (puede recibir y hacer daño)
- 🎮 **Comando `/pvp off`** - Desactiva el PvP para el jugador (no puede recibir ni hacer daño)
- ⚙️ **Server-side only** - No requiere instalación en el cliente
- 🔧 **Basado en Gradle** - Fácil de compilar y modificar

## Funcionamiento

### PvP Habilitado (`/pvp on`)
- El jugador puede recibir daño de otros jugadores
- El jugador puede hacer daño a otros jugadores

### PvP Deshabilitado (`/pvp off`)
- El jugador **NO** puede recibir daño de otros jugadores
- El jugador **NO** puede hacer daño a otros jugadores

## Instalación

1. **Descargar Forge 1.19.2**
   - Ve a [forge.minecraftforge.net](https://files.minecraftforge.net/net/minecraftforge/forge)
   - Descarga la versión 43.2.0 o superior para 1.19.2

2. **Compilar el mod**
   ```bash
   # En Windows
   gradlew.bat build
   
   # En Linux/Mac
   ./gradlew build
   ```

3. **Ubicar el JAR compilado**
   - El archivo se generará en: `build/libs/togglepvp-1.0.0.jar`

4. **Instalar en el servidor**
   - Copia el JAR a la carpeta `mods/` de tu servidor Minecraft
   - Reinicia el servidor

## Comandos

```
/pvp on     - Activa el PvP para ti
/pvp off    - Desactiva el PvP para ti
```

## Requisitos

- **Minecraft**: 1.19.2
- **Forge**: 43.2.0 o superior
- **Java**: 17 o superior
- **Gradle**: 7.5.1 (incluido en el wrapper)

## Estructura del Proyecto

```
TogglePVP/
├── src/
│   └── main/
│       ├── java/com/justinm/togglepvp/
│       │   ├── TogglePVPMod.java              # Clase principal del mod
│       │   ├── handler/
│       │   │   └── PVPToggleHandler.java      # Gestor de estados PvP
│       │   ├── event/
│       │   │   └── PVPEventHandler.java       # Listeners de eventos
│       │   └── command/
│       │       └── PVPCommand.java            # Registro de comandos
│       └── resources/
│           └── META-INF/
│               └── mods.toml                  # Metadata del mod
├── build.gradle                               # Configuración de Gradle
├── gradle.properties                          # Propiedades de Gradle
└── gradlew[.bat]                              # Gradle Wrapper
```

## Desarrollo

### Compilar
```bash
./gradlew build
```

### Ejecutar servidor de desarrollo
```bash
./gradlew runServer
```

### Ejecutar cliente de desarrollo
```bash
./gradlew runClient
```

## Cómo Funciona

### PVPToggleHandler
Maneja un HashMap con el estado PvP de cada jugador por UUID. Por defecto, todos tienen PvP habilitado.

### PVPEventHandler
- **onServerStarting**: Registra el comando `/pvp` al iniciar el servidor
- **onPlayerDamage**: Intercepta eventos de daño y verifica si ambos jugadores tienen PvP habilitado
- **onPlayerLogout**: Limpia los datos del jugador cuando se desconecta

## Permisos

El mod no requiere permisos especiales. Todos los jugadores pueden usar los comandos `/pvp on` y `/pvp off`.

## Compatibilidad

- ✅ Multiplayer (servidor)
- ✅ Singleplayer con LAN
- ✅ Compatible con otros mods
- ✅ No requiere cliente-side

## Changelog

### v1.0.0
- Implementación inicial
- Comando `/pvp on` y `/pvp off`
- Gestión de daño entre jugadores
- Limpieza de datos al desconectar

## Licencia

MIT

## Autor

Justin Martinez

## Soporte

Para reportar problemas o sugerencias, abre un issue en el repositorio.