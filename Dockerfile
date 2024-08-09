# Docker para desarrollo
FROM gradle:8.9.0-jdk21

ENV RABBIT_URL=host.docker.internal
ENV MONGO_URL=mongodb://host.docker.internal
ENV AUTH_SERVICE_URL=http://host.docker.internal:3000

WORKDIR /app

# Puerto de catalog service
EXPOSE 3002

CMD gradle run
