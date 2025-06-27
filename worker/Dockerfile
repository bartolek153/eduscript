# https://github.com/hemicharly/grpc-spring-boot-3/blob/main/grpc-server/Dockerfile

FROM public.ecr.aws/docker/library/eclipse-temurin:21.0.3_9-jre-alpine

RUN apk add --no-cache dumb-init && \
    rm -rf /var/cache/apk/*

ENV JAVA_USER=javauser
ENV WORKDIR_HOME=/app

ENV SPRING_KAFKA_BOOTSTRAP_SERVERS=broker:29092
ENV SPRING_DATA_REDIS_HOST=cache

RUN mkdir ${WORKDIR_HOME} &&\
    addgroup --system ${JAVA_USER} && adduser -S -s /bin/false -G ${JAVA_USER} ${JAVA_USER} &&\
    chown ${JAVA_USER}:${JAVA_USER} ${WORKDIR_HOME}

WORKDIR  ${WORKDIR_HOME}

COPY --chown=${JAVA_USER}:${JAVA_USER} target/worker*.jar ./application.jar

USER ${JAVA_USER}

EXPOSE 9090
EXPOSE 8080

CMD ["dumb-init", "java", "-jar", "application.jar"]