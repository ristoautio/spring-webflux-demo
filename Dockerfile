# Build stage with Ubuntu and GraalVM
FROM ubuntu:22.04 AS build

# Set environment to avoid interactive prompts
ENV DEBIAN_FRONTEND=noninteractive

# Install required packages
RUN apt-get update && \
    apt-get install -y wget curl unzip gcc g++ libc6-dev zlib1g-dev && \
    rm -rf /var/lib/apt/lists/*

# Install GraalVM manually
ENV JAVA_HOME=/opt/graalvm
ENV PATH="$JAVA_HOME/bin:$PATH"

# Download and install GraalVM for the correct architecture
RUN ARCH=$(uname -m | sed 's/aarch64/aarch64/;s/x86_64/x64/') && \
    echo "Detected architecture: $ARCH" && \
    wget -O graalvm.tar.gz "https://download.oracle.com/graalvm/21/latest/graalvm-jdk-21_linux-${ARCH}_bin.tar.gz" && \
    mkdir -p /opt && \
    tar -xzf graalvm.tar.gz -C /opt && \
    rm graalvm.tar.gz && \
    mv /opt/graalvm-* $JAVA_HOME

# Verify Java installation and install native-image
RUN java -version

WORKDIR /app

# Copy Maven files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source and build
COPY src src

# Build native image
RUN ./mvnw clean package -Pnative -DskipTests

# Set permissions in build stage to avoid layer duplication
RUN chmod +x /app/target/spring-webflux-demo

FROM alpine:3.20

# Install minimal runtime dependencies + gcompat for glibc compatibility
RUN apk add --no-cache curl ca-certificates gcompat && \
    addgroup -g 1001 spring && \
    adduser -D -s /bin/sh -u 1001 -G spring spring

WORKDIR /app

# Copy with correct ownership directly (no chmod/chown needed)
COPY --from=build --chown=spring:spring /app/target/spring-webflux-demo app

USER spring

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the native application
ENTRYPOINT ["./app"]
