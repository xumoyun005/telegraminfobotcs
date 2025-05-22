FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apk add --no-cache maven

# Build the application
RUN mvn clean package -DskipTests

# Run the application
CMD ["java", "-jar", "target/InfoBotCs-1.0-SNAPSHOT.jar"] 