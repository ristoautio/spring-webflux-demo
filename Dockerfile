# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copy and build application
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw clean package -DskipTests

# Extract application for dependency analysis
RUN mkdir -p target/dependency && cd target/dependency && jar -xf ../*.jar

# Determine required modules
RUN jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 21 \
    --print-module-deps \
    --class-path 'target/dependency/BOOT-INF/lib/*' \
    target/*.jar > jre-modules.txt && cat jre-modules.txt

# Set permissions in build stage to avoid layer duplication
RUN chmod +x target/*.jar

# Add essential modules for Spring Boot
#RUN echo "java.base,java.logging,java.xml,java.desktop,java.management,java.security.jgss,java.instrument,java.net.http,java.security.sasl,jdk.unsupported,jdk.crypto.ec" >> jre-modules.txt

# Create minimal JRE
RUN cat jre-modules.txt | tr '\n' ',' | sed 's/,$//' > modules.list && \
    jlink --add-modules $(cat modules.list) \
          --strip-debug \
          --compress 2 \
          --no-header-files \
          --no-man-pages \
          --output /custom-jre

# Runtime stage
FROM alpine:3.18

# Install required packages
RUN apk add --no-cache \
    curl \
    gcompat \
    && addgroup -g 1001 spring \
    && adduser -D -s /bin/sh -u 1001 -G spring spring

# Set up Java
ENV JAVA_HOME=/opt/java
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copy JRE
COPY --from=build /custom-jre $JAVA_HOME

# Verify Java installation
RUN java -version

WORKDIR /app

# Copy application
#COPY --from=build /app/target/*.jar app.jar
#RUN chown spring:spring app.jar

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

USER spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
