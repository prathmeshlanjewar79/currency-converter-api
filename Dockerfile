# --- Build stage ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/currency-converter-api.jar app.jar

# Render sets $PORT at runtime; Spring Boot picks it up via this env var.
ENV SERVER_PORT=${PORT:-8080}
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
