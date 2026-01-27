FROM maven:3.9.12-eclipse-temurin-25 AS builder
WORKDIR /app

COPY platform-bom/ ./platform-bom/
RUN mvn -f platform-bom/pom.xml install -DskipTests

COPY transaction-service/ ./transaction-service/
WORKDIR /app/transaction-service

RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/transaction-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]