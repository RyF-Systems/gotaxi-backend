# Multi-stage Dockerfile: build with Gradle wrapper, run on Eclipse Temurin JRE 17

FROM eclipse-temurin:17 as builder
WORKDIR /workspace

# Copy project files and build
COPY . .
RUN chmod +x gradlew || true
RUN ./gradlew clean bootJar -x test --no-daemon

# Collect the built jar into a stable path
RUN mkdir -p /output && cp build/libs/*.jar /output/app.jar

# Runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /output/app.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
