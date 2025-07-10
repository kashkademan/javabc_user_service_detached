# syntax=docker/dockerfile:1
FROM openjdk:17-jdk-slim-buster
WORKDIR /app
COPY /build/libs/service.jar /app/user-service.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "user-service.jar", "--spring.profiles.active=prod"]
