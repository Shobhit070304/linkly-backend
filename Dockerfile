# Use Java 17
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy Maven wrapper
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8000

# Run the application
CMD ["java", "-jar", "target/linkly-backend-0.0.1-SNAPSHOT.jar"]