# Build stage
FROM eclipse-temurin:17-jdk AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and POM file
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy only the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8000

# Create non-root user for security
RUN useradd -m -u 1001 appuser && chown -R appuser:appuser /app
USER appuser

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]