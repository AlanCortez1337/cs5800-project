# 1. Base Builder Stage (Use a dedicated Maven image)
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# 2. Copy POM files first for dependency downloading
# This step is cached as long as your pom.xml/pom.properties don't change
COPY pom.xml .
# If you have a multi-module project, you need to copy all pom files
# COPY **/pom.xml ./

# Download all project dependencies
RUN mvn dependency:go-offline -B

# 3. Copy source code and build the application
COPY src ./src
# Now the build step runs using the cached dependencies
RUN mvn package -DskipTests

# 4. Final Runner Stage (Slimmer image)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]