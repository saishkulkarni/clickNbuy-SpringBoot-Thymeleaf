# Build stage
FROM maven:3.9.5-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV SERVER_PORT=8080

EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]