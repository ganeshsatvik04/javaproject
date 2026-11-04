# =============================================================
# Stage 1: Build the React / Vite Web Frontend
# =============================================================
FROM node:20-alpine AS frontend-build
WORKDIR /frontend
ENV NODE_ENV=development
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# =============================================================
# Stage 2: Build the Java Backend with Embedded Frontend Assets
# =============================================================
FROM maven:3.9.6-eclipse-temurin-17 AS backend-build
WORKDIR /backend
COPY CareerMatch_Backend/pom.xml .
COPY CareerMatch_Backend/src ./src

# Inject built frontend assets into Javalin public classpath
RUN mkdir -p ./src/main/resources/public
COPY --from=frontend-build /frontend/dist ./src/main/resources/public

RUN mvn clean package -DskipTests

# =============================================================
# Stage 3: Lightweight Production JRE Runtime
# =============================================================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the standalone executable fat JAR
COPY --from=backend-build /backend/target/careermatch-backend.jar app.jar

# Setup directory for persistent SQLite database
RUN mkdir -p /app/data
ENV DB_PATH=/app/data/careermatch.db
ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
