# Use Java 17 slim image
FROM openjdk:17-jdk-slim

# Working directory inside the container
WORKDIR /app

# Install curl for downloading dependencies
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Download MySQL Connector/J (JDBC driver)
RUN curl -L -o mysql-connector-j.jar \
  https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar

# Copy your Java source files into the container
COPY . .

# Compile the demo CLI (you can add Main.java if needed)
RUN javac -cp mysql-connector-j.jar DemoCLI.java

# Wait for MySQL to be ready, then run
CMD bash -lc 'for i in {1..60}; do echo > /dev/tcp/db/3306 2>/dev/null && break || echo "Waiting for MySQL..."; sleep 2; done; \
  java -cp .:mysql-connector-j.jar DemoCLI'
