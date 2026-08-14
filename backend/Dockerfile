# syntax=docker/dockerfile:1
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress clean package

FROM eclipse-temurin:21-jre-noble
RUN apt-get update \
    && apt-get install --yes --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --system --uid 10001 --create-home campuscare
WORKDIR /app
COPY --from=build /workspace/target/campuscare-helpdesk-api-*.jar app.jar
USER campuscare
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
