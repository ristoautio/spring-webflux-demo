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
#&& \
 #   $JAVA_HOME/bin/gu install native-image

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

# Runtime stage - Use Ubuntu instead of Alpine for glibc compatibility
FROM ubuntu:22.04

# Install minimal runtime dependencies
RUN apt-get update && \
    apt-get install -y curl ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -r spring && useradd -r -g spring spring

WORKDIR /app

# Copy native executable
COPY --from=build /app/target/spring-webflux-demo app

# Make executable and change ownership
RUN chmod +x app && chown spring:spring app

USER spring

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the native application
ENTRYPOINT ["./app"]
