FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Install shared-events library from local copy
COPY common-dto-library /build/lib
RUN cd /build/lib && mvn clean install -DskipTests -q

# Copy pom.xml and download dependencies
COPY app-microservice-auth/pom.xml .
RUN mvn dependency:go-offline -q

# Copy source code and build
COPY app-microservice-auth/src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8000

# JVM optimized for t3.micro (1GB RAM total, ~200MB heap per service)
ENTRYPOINT ["java", "-Xms64m", "-Xmx192m", "-XX:+UseSerialGC", "-XX:MaxGCPauseMillis=200", "-jar", "app.jar"]
