FROM ghcr.io/navikt/baseimages/temurin:18

COPY build/libs/fat.jar ./app.jar
COPY agents/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENV JAVA_OPTS -javaagent:opentelemetry-javaagent.jar
CMD ["java", "$JAVA_OPTS", "-jar", "/Application.jar"]