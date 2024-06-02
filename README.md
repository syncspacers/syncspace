# SyncSpace

## Índice
- [Configurar el entorno](#configurar)
- [Compilar](#compilar)
- [Ejecutar](#ejecutar)
- [Pasar pruebas](#pruebas)

# Configurar
Se necesita tener Java 17 instalado. Puede ser necesario configurar la variable de entorno JAVA_HOME. Por ejemplo:
- En Windows (probado con Oracle JDK), en "Editar variables de entorno", JAVA_HOME = C:\Program Files\Java\jdk-17
- En Linux: "export JAVA_HOME=/usr/lib/jdk-17"
IMPORTANTE: La variable JAVA_HOME debe ser un directorio que contenga un directorio `bin`, y dentro de este tiene que estar java.exe, javac.exe, y demás.

# Compilar
- En windows: `mvnw.cmd package`
- En Linux: `./mvnw package`

Al terminar el proceso, se mostrará la ruta del `.jar` generado

# Ejecutar
Una vez compilado el proyecto: `java -jar ruta/a/syncspace.jar`, por ejemplo: `java -jar target/syncspace-0.0.1-SNAPSHOT.jar`

# Pruebas
Para pasar la batería de pruebas al proyecto en Linux y Windows: `./mvnw test` ó `mvnw.cmd test`, respectivamente.
