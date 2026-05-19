# =========================================
# BUILD STAGE
# =========================================

FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /dmx

# Copiar wrapper y pom primero para cache
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Descargar dependencias
RUN chmod +x mvnw
RUN ./mvnw dependency:resolve

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN ./mvnw clean package -DskipTests

# =========================================
# RUNTIME STAGE
# =========================================

FROM eclipse-temurin:21-jre

WORKDIR /dmx

# Variables JVM
ENV JAVA_OPTS=""

# Copiar JAR compilado
COPY --from=build /dmx/target/*.jar dmx.jar

# Exponer puerto
EXPOSE 8080

# Ejecutar aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar dmx.jar"]