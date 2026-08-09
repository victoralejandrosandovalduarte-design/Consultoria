# =============================================
# Stage 1: Build
# =============================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom first (cache dependencies layer)
COPY pom.xml .
COPY src ./src

# Install Maven and build the project (skip tests for image build)
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# =============================================
# Stage 2: Runtime
# =============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/TI-1.0.0.jar app.jar

# Change ownership to non-root user
RUN chown appuser:appgroup app.jar

USER appuser

# Expose the application port
EXPOSE 8090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
