FROM ghcr.io/navikt/baseimages/temurin:21

COPY ./build/libs/fat.jar /app.jar
COPY agents/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENV JAVA_OPTS -javaagent:opentelemetry-javaagent.jar -Dotel.resource.attributes=service.name=paw-arbeidssoker-besvarelse
CMD ["java", "$JAVA_OPTS", "-jar", "/Application.jar"]
