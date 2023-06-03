# Spring Dockerization > https://github.com/spring-guides/top-spring-boot-docker
# Base Image > https://hub.docker.com/r/adoptopenjdk/openjdk11/tags?page=1&name=jre-11.0.14.1_1-alpine
ARG BASE_REGISTRY="docker.io/adoptopenjdk"
ARG BASE_IMG_NAME="openjdk11"
ARG BASE_IMG_VERS="jre-11.0.14.1_1-alpine"
FROM ${BASE_REGISTRY}/${BASE_IMG_NAME}:${BASE_IMG_VERS}

ARG APP_NAME="flightspecials"

COPY interface/build/libs/${APP_NAME}.jar /source001/boot.jar
RUN chmod +x /source001/boot.jar

ENV LC_ALL C.UTF-8
ENV PROFILE "default"
ENV JAVA_HEAP_XMS "512m"
ENV JAVA_HEAP_XMX "512m"
ENV AWS_XRAY_CONTEXT_MISSING "LOG_ERROR"

COPY docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

ENTRYPOINT ["./docker-entrypoint.sh"]
