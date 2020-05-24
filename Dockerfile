# Use node 
FROM gradle:6.4.1-jdk11

# Copy source code
COPY . /app

# Change working directory
WORKDIR /app

# Install dependencies
RUN gradle build

# Expose API port to the outside
EXPOSE 3002

# Launch application
CMD ["gradle","run"]
