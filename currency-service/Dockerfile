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

FROM eclipse-temurin:25-jdk

WORKDIR /app

# Copy your whole project into the container
COPY . .

RUN chmod +x mvnw

# Use the wrapper to build instead of "mvn"
RUN ./mvnw clean package -DskipTests

# Run the jar (assuming the jar ends up in target/app.jar)
ENTRYPOINT ["java", "-jar", "currency-service/target/currency-service-0.0.1-SNAPSHOT.jar"]