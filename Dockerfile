FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

COPY build.gradle settings.gradle gradle.properties ./

COPY gradlew* ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon || true

COPY src ./src

RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/franchises || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
