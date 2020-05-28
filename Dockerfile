# Docker para desarrollo
FROM gradle:6.4.1-jdk11

ENV RABBIT_URL host.docker.internal
ENV MONGO_URL host.docker.internal
ENV AUTH_SERVICE_URL http://host.docker.internal:3000

WORKDIR /app

# Puerto de catalog service
EXPOSE 3002

CMD gradle run
