# Compilación del Mod Toggle PVP

## Requisitos Previos

Antes de compilar, asegúrate de tener instalado:

1. **Java Development Kit (JDK) 17 o superior**
   - Descarga desde: https://www.oracle.com/java/technologies/downloads/
   - O usa OpenJDK: https://adoptium.net/

2. **Gradle** (Opcional - el wrapper ya está incluido)
   - El proyecto incluye `gradlew.bat` (Windows) y `gradlew` (Linux/Mac)

## Pasos para Compilar

### Opción 1: Usando Gradle Wrapper (Recomendado)

#### En Windows (PowerShell o CMD):
```bash
cd C:\Users\JustinM\Desktop\TogglePVP
gradlew.bat build
```

#### En Linux/Mac:
```bash
cd ~/Desktop/TogglePVP
chmod +x gradlew
./gradlew build
```

### Opción 2: Usando Gradle Instalado Globalmente
```bash
gradle build
```

## Ubicación del JAR Compilado

Después de compilar, el mod se encontrará en:
```
build/libs/togglepvp-1.0.0.jar
```

## Instalación en el Servidor

1. Localiza la carpeta `mods` de tu servidor Minecraft Forge 1.19.2
2. Copia `togglepvp-1.0.0.jar` a esa carpeta
3. Reinicia el servidor

## Solución de Problemas

### Error: "JAVA_HOME no está configurado"
**Solución**: Establece la variable de entorno JAVA_HOME
- Windows: Ver variables de entorno del sistema
- Linux/Mac: Añade a ~/.bashrc o ~/.zshrc:
  ```bash
  export JAVA_HOME=/ruta/a/java
  ```

### Error: "Could not find build tools"
**Solución**: Ejecuta primero:
```bash
./gradlew clean
./gradlew build
```

### Error de Gradle daemon
**Solución**: Limpia el daemon:
```bash
./gradlew --stop
./gradlew clean build
```

## Desarrollo

### Ejecutar el servidor de desarrollo
```bash
./gradlew runServer
```

### Ejecutar el cliente de desarrollo
```bash
./gradlew runClient
```

### Limpiar la compilación anterior
```bash
./gradlew clean
```

## Estructura de Carpetas Después de la Compilación

```
build/
├── classes/
├── libs/
│   └── togglepvp-1.0.0.jar  ← Tu mod compilado
└── resources/
```

## Verificar la Compilación

Para verificar que la compilación fue exitosa:

```bash
# Windows
dir build\libs\

# Linux/Mac
ls -la build/libs/
```

Deberías ver el archivo `togglepvp-1.0.0.jar`

## Notas Importantes

- ⚠️ El mod es **server-side**, no necesita instalarse en el cliente
- ⚠️ Requiere **Forge 43.2.0 o superior**
- ⚠️ Requiere **Minecraft 1.19.2**
- ⚠️ Requiere **Java 17 o superior**

## Contacto y Soporte

Si tienes problemas con la compilación, verifica:
1. Que Java 17+ esté correctamente instalado: `java -version`
2. Que JAVA_HOME apunte a la instalación correcta
3. Que tengas conexión a internet (Gradle descargará dependencias)
4. Los archivos de configuración no estén corruptos
