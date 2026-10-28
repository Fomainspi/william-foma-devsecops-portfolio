# Build the JAR first with: mvn clean package
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run as the unprivileged account supplied by the base image.
COPY --chown=1000:1000 target/portfolio.jar /app/app.jar
USER 1000:1000

EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Djava.io.tmpdir=/tmp"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
