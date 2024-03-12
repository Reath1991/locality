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

# Install Consul by HashiCorp
# Download Consul binary and install
RUN wget -O /usr/local/bin/consul https://releases.hashicorp.com/consul/1.11.0/consul_1.11.0_linux_amd64.zip && \
    chmod +x /usr/local/bin/consul
# Copy Spring Boot application JAR from the builder stage
COPY --from=builder /app/target/*.jar /app/configservice.jar

# Expose ports (Spring Boot: 8080, Consul: 8500)
EXPOSE 8080 8500

# Command to run the application
CMD ["java", "-jar", "/app/configservice.jar"]
