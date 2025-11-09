# Use Eclipse Temurin OpenJDK 17 as the base image
FROM eclipse-temurin:25-jdk

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# ✅ Give execute permission to mvnw
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Run the built JAR
CMD ["java", "-jar", "target/womensafety-0.0.1-SNAPSHOT.jar"]
