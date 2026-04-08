FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

# pulo do gato - builda o projeto inteiro (instala parent + shared)
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=build /app/api_gateway/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

USER appuser

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]