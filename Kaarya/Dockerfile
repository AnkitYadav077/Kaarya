FROM openjdk:22-jdk-slim

# Install netcat and curl for health checks
RUN apt-get update && apt-get install -y netcat-openbsd curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy wait script
COPY wait-for-kafka.sh wait-for-kafka.sh
RUN chmod +x wait-for-kafka.sh

# Copy your built jar
COPY target/Kaarya.jar Kaarya.jar

EXPOSE 8080

# Wait for Kafka to be ready before starting the application
CMD ["./wait-for-kafka.sh", "kafka", "9092", "java", "-jar", "Kaarya.jar"]