FROM openjdk:17-jdk

WORKDIR /app/src/security

COPY target/security-0.0.1-SNAPSHOT.jar /app/src/security/security.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/src/security/security.jar"]
