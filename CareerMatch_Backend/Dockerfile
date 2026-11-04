# Multi-stage Maven build for CareerMatch Java Backend
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Production JRE runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/careermatch-backend.jar app.jar

# Setup directory for persistent SQLite volume on Railway
RUN mkdir -p /app/data
ENV DB_PATH=/app/data/careermatch.db
ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
