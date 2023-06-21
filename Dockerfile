FROM ghcr.io/navikt/baseimages/temurin:18

COPY build/libs/fat.jar ./app.jar
