# Etapa 1: Build
FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle settings.gradle gradle.properties ./

# Copiar gradlew y carpeta gradle wrapper (si existen)
COPY gradlew* ./
COPY gradle ./gradle

# Descargar dependencias (cacheable)
RUN gradle dependencies --no-daemon || true

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN gradle clean bootJar --no-daemon

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear usuario no-root para mayor seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR desde la etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Configurar health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/franchises || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
