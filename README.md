# SyncSpace

## Índice
- [Compilar](#compilar)
- [Ejecutar](#ejecutar)
- [Pasar pruebas](#pruebas)

# Compilar
- En windows: `./mvnw.cmd package`
- En Linux: `./mvnw package`

Al terminar el proceso, se mostrará la ruta del `.jar` generado

# Ejecutar
Una vez compilado el proyecto: `java -jar ruta/a/syncspace.jar`, por ejemplo: `java -jar target/syncspace-0.0.1-SNAPSHOT.jar`

# Pruebas
Para pasar la batería de pruebas al proyecto en Linux y Windows: `./mvnw test` ó `./mvnw.cmd test`, respectivamente.
