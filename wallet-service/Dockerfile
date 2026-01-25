#
# Build
#
FROM maven:eclipse-temurin:25 AS build

WORKDIR /usr/src/app

COPY . ./

RUN mvn clean package -Dmaven.test.skip

#
# Package
#
FROM eclipse-temurin:25

WORKDIR /usr/src/app

COPY --from=build /usr/src/app/target/*.jar ./app.jar

ENTRYPOINT ["java", "-jar", ".app.jar"]