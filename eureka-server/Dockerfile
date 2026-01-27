##
## Build
##
#FROM maven:3.9.12-eclipse-temurin-25 AS build
#
#WORKDIR /usr/src/app
#
#COPY . ./
#
#COPY ../pom.xml ../pom.xml
#
#RUN mvn clean package -Dmaven.test.skip
#
##
## Package
##
#FROM eclipse-temurin:25
#
#WORKDIR /usr/src/app
#
#COPY --from=build /usr/src/app/target/*.jar ./app.jar
#
#ENTRYPOINT ["java", "-jar", "./app.jar"]



#FROM eclipse-temurin:25-jdk
#
#WORKDIR /app
#
## Copy your whole project into the container
#COPY . .
#
#RUN chmod +x mvnw
#
## Use the wrapper to build instead of "mvn"
#RUN ./mvnw clean package -B -ntp -DskipTests
#
## Run the jar (assuming the jar ends up in target/app.jar)
#ENTRYPOINT ["sh", "-c", "java -jar eureka-server/target/*.jar"]
##ENTRYPOINT ["java", "-jar", "discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar"]
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app

# 1. Copy the Maven wrapper (from the root or service folder)
COPY .mvn/ .mvn
COPY mvnw ./
RUN chmod +x mvnw

# 2. Copy the BOM folder and INSTALL it locally in the container
# This is the "Fix" for the Non-resolvable error
COPY platform-bom/ ./platform-bom/
RUN ./mvnw -f platform-bom/pom.xml install -DskipTests

# 3. Now copy the Eureka Server source
COPY eureka-server/ ./eureka-server/
WORKDIR /app/eureka-server

# 4. Build the actual service
# It will now find the BOM in the container's local repository
RUN ../mvnw clean package -DskipTests

# --- STAGE 2: Tiny Runtime ---
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/eureka-server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]