# Use the official Maven image for a build stage
FROM maven:3.8.4-openjdk-17 as build

# Copy the pom.xml and source code
COPY ./pom.xml ./pom.xml
COPY ./src ./src

# Package the application
RUN mvn clean package -DskipTests

# Use OpenJDK for the final image
FROM openjdk:17-alpine

# Copy the jar from the build stage
COPY --from=build /target/*.jar /app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app.jar"]