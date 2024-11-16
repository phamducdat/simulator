#
# Build stage
#
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
RUN apk update && apk add gcompat
WORKDIR /app
COPY pom.xml ./pom.xml
RUN --mount=type=cache,target=/root/.m2,rw mvn dependency:go-offline -B
COPY src ./src
RUN --mount=type=cache,target=/root/.m2,rw mvn -Dmaven.test.skip=true clean package

#
# Package stage
#
FROM amazoncorretto:21.0.2-alpine3.19
COPY newrelic /usr/local/lib/newrelic
COPY --from=build /app/target/lotusmile-account-service-0.0.1-SNAPSHOT.jar /usr/local/lib/lotusmile-account-service.jar
EXPOSE 8084
ENTRYPOINT ["sh", "-c", "java -Dnewrelic.environment=$ENV -javaagent:/usr/local/lib/newrelic/newrelic.jar -jar /usr/local/lib/lotusmile-account-service.jar"]