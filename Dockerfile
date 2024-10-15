FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-alpine3.14

WORKDIR /app

COPY --from=build /app/target/tiny-url-app-1.0.0.jar /app/tinyurl-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/tinyurl-app.jar", "--spring.config.location=file:/app/application.properties"]