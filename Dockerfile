# Stage 2: Build the Spring Boot application
FROM eclipse-temurin:21-jdk-jammy AS spring-boot-build
WORKDIR /workspace/app

# Declare ARG for server.url and JWT_SECRET
ARG SERVER_URL
ARG JWT_SECRET

# Set environment variables for the runtime stage
ENV JWT_SECRET=$JWT_SECRET
ENV SERVER_URL=$SERVER_URL

# Install Maven and other necessary tools
RUN apt-get update \
  && apt-get install -y ca-certificates curl git --no-install-recommends \
  && rm -rf /var/lib/apt/lists/*

# Set Maven environment variables
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "/root/.m2"
COPY .env /workspace/app/.env

# Copy Maven from the official Maven Docker image
COPY --from=maven:3.9.7-eclipse-temurin-11 ${MAVEN_HOME} ${MAVEN_HOME}
COPY --from=maven:3.9.7-eclipse-temurin-11 /usr/local/bin/mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY --from=maven:3.9.7-eclipse-temurin-11 /usr/share/maven/ref/settings-docker.xml /usr/share/maven/ref/settings-docker.xml

# Link Maven to the bin directory for easy execution
RUN ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

# Copy the Spring Boot application's pom.xml and source code
COPY ./postcode/pom.xml .
COPY ./postcode/src src

# Copy Maven Wrapper files
COPY ./postcode/.mvn .mvn
COPY ./postcode/mvnw .
COPY ./postcode/mvnw.cmd .

# Ensure mvnw is executable
RUN chmod +x ./mvnw

# Build the Spring Boot application without running tests
RUN mvn install -DskipTests -P dev -q

# Stage 3: Run the Spring Boot application
FROM eclipse-temurin:21
WORKDIR /app
COPY --from=spring-boot-build /workspace/app/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]