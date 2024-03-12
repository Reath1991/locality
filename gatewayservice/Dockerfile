# Stage 1: Build Spring Boot application
FROM maven:3.8.4-openjdk-17-slim AS builder

# Set working directory
WORKDIR /app

# Copy Maven configuration and project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Setup runtime environment
FROM openjdk:17-slim

# Install PostgreSQL client
RUN apt-get update && apt-get install -y postgresql-client

# Install wget and unzip
RUN apt-get install -y wget unzip


COPY --from=builder /app/target/*.jar /app/gatewayservice.jar

# Expose ports (Spring Boot: 8080, Consul: 8500)
EXPOSE 8081

# Command to run the application
CMD ["java", "-jar", "/app/gatewayservice.jar"]
