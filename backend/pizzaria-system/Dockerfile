# Etapa 1: Build da aplicação
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Imagem final (somente o JAR)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
