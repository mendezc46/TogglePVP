# Guía de Desarrollo y Extensión del Mod Toggle PVP

## Descripción General de la Arquitectura

El mod está dividido en 4 componentes principales:

### 1. **TogglePVPMod.java** - Clase Principal
La clase anotada con `@Mod("togglepvp")` que inicializa el mod.

```java
@Mod("togglepvp")
public class TogglePVPMod {
    public static final String MOD_ID = "togglepvp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
}
```

### 2. **PVPToggleHandler.java** - Gestor de Estado
Mantiene un HashMap con el estado PVP de cada jugador usando su UUID como clave.

**Métodos disponibles:**
- `isPVPEnabled(UUID)` - Verifica si un jugador tiene PVP habilitado
- `setPVPStatus(UUID, boolean)` - Establece el estado PVP
- `togglePVP(UUID)` - Alterna el estado PVP
- `removePlayer(UUID)` - Elimina los datos del jugador

### 3. **PVPEventHandler.java** - Listeners de Eventos
Contiene los listeners que escuchan eventos del servidor.

**Eventos implementados:**
- `ServerStartingEvent` - Registra los comandos
- `LivingDamageEvent` - Previene daño según el estado PVP
- `PlayerLoggedOutEvent` - Limpia datos al desconectar

### 4. **PVPCommand.java** - Registro de Comandos (Placeholder)
Archivo reservado para expansión futura.

## Cómo Extender el Mod

### Agregar un Comando Nuevo

1. En `PVPEventHandler.java`, dentro de `onServerStarting()`:

```java
dispatcher.register(Commands.literal("pvpstatus")
    .executes(context -> {
        ServerPlayer player = context.getSource().getPlayerOrException();
        boolean isPVPEnabled = PVPToggleHandler.isPVPEnabled(player.getUUID());
        
        String status = isPVPEnabled ? "ACTIVADO" : "DESACTIVADO";
        player.displayClientMessage(
            Component.literal("§eTu estado PVP: §6" + status),
            false
        );
        return 1;
    })
);
```

### Agregar Mensajes Configurables

1. Crea un archivo de configuración `config.toml` en `src/main/resources/`
2. Carga la configuración en la clase principal

### Agregar Persistencia de Datos

Para que los datos persistan entre reinicio del servidor:

```java
// En PVPToggleHandler.java
private static final File DATA_FILE = new File("./config/togglepvp/playerdata.json");

public static void savePlayerData() {
    // Serializar HashMap a JSON
}

public static void loadPlayerData() {
    // Cargar JSON al HashMap
}
```

### Agregar Permisos

Para integrar un sistema de permisos como LuckPerms:

```java
// En PVPEventHandler.java - onServerStarting()
dispatcher.register(Commands.literal("pvpadmin")
    .requires(source -> source.hasPermission(4)) // Solo OPs
    .then(Commands.argument("player", EntityArgument.player())
        .then(Commands.argument("status", StringArgumentType.word())
            .executes(context -> {
                // Toggle PVP para otro jugador
            })
        )
    )
);
```

### Agregar Configuración de Tiempo de Recarga

```java
// Agregar a PVPEventHandler.java
private static final long PVP_COOLDOWN_MS = 5000; // 5 segundos
private static final Map<UUID, Long> lastToggle = new HashMap<>();

// En el comando /pvp:
long now = System.currentTimeMillis();
long lastTime = lastToggle.getOrDefault(playerUUID, 0L);

if (now - lastTime < PVP_COOLDOWN_MS) {
    player.displayClientMessage(
        Component.literal("§cDebes esperar antes de cambiar PVP nuevamente"),
        false
    );
    return 0;
}

lastToggle.put(playerUUID, now);
```

### Agregar Notificaciones a Otros Jugadores

```java
// En PVPEventHandler.java - dentro del comando
if ("on".equalsIgnoreCase(action)) {
    PVPToggleHandler.setPVPStatus(player.getUUID(), true);
    
    // Notificar a jugadores cercanos
    List<ServerPlayer> nearby = player.getServer().getPlayerList()
        .getPlayers()
        .stream()
        .filter(p -> p.distanceTo(player) < 50)
        .collect(Collectors.toList());
    
    for (ServerPlayer nearby : nearby) {
        nearby.displayClientMessage(
            Component.literal(player.getName().getString() + " ha habilitado PVP"),
            true // actionbar
        );
    }
}
```

## Testing

Para probar el mod en desarrollo:

1. Ejecuta el servidor de desarrollo:
   ```bash
   ./gradlew runServer
   ```

2. Conecta con un cliente (o ejecuta `./gradlew runClient`)

3. Usa los comandos `/pvp on` y `/pvp off`

4. Verifica los logs en `logs/latest.log`

## Debugging

Para habilitar logs detallados, edita `build.gradle`:

```gradle
minecraft {
    runs {
        server {
            property 'forge.logging.level.togglepvp', 'debug'
        }
    }
}
```

## Cambios Frecuentes

### Cambiar la Versión del Mod
Edita `build.gradle`:
```gradle
version = '1.1.0'  // Cambia aquí
```

### Cambiar el Identificador
Requiere cambios en:
- `build.gradle` → `group = 'com.justinm.togglepvp'`
- `@Mod("togglepvp")` en TogglePVPMod.java
- `mods.toml` y `pack.mcmeta`

### Agregar Dependencias
En `build.gradle`, sección `dependencies`:
```gradle
dependencies {
    minecraft 'net.minecraftforge:forge:1.19.2-43.2.0'
    // Agregar aquí
}
```

## Referencias Útiles

- **Forge Docs**: https://docs.minecraftforge.net/
- **Minecraft Wiki**: https://minecraft.wiki/
- **Brigadier**: https://github.com/Mojang/brigadier
- **Event Bus**: https://docs.minecraftforge.net/en/latest/javadocs/net/minecraftforge/eventbus/api/Event.html

## Preguntas Frecuentes

**¿Cómo cambio el nombre del comando?**
Cambia `Commands.literal("pvp")` por lo que quieras en `PVPEventHandler.java`.

**¿Cómo agriego más opciones al comando?**
Usa `.then()` para agregar sub-comandos en el registro de comandos.

**¿Cómo hago que el mod sea client-side?**
Mueve los listeners a `@Mod.EventBusSubscriber(value = Dist.CLIENT)`.

**¿Cómo guardo datos entre sesiones?**
Implementa serialización JSON o NBT en el `PVPToggleHandler`.
