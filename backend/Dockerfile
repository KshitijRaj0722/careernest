# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the POM first so dependency resolution is cached and only re-runs when
# dependencies actually change, not on every source edit.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
# Tests are skipped here: they boot the Spring context, which needs live MongoDB
# credentials that are not (and should not be) available at image build time.
RUN mvn -B clean package -DskipTests

# ---- Runtime stage ----
# JRE-only base keeps the final image far smaller than the build image.
FROM eclipse-temurin:17-jre
WORKDIR /app

# Run as a non-root user.
RUN useradd --create-home --shell /bin/bash appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

# Render/Railway inject $PORT; application.properties binds to it, defaulting to 8080.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
