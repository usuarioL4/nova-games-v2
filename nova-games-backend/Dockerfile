# syntax=docker/dockerfile:1
# Compile the whole Maven reactor once; each runtime image selects one executable jar.
FROM maven:3.9.9-eclipse-temurin-17 AS build
ARG SERVICE
WORKDIR /workspace
COPY . .
RUN mvn -B -ntp -pl "${SERVICE}" -am clean package

FROM eclipse-temurin:17-jre-jammy AS runtime
ARG SERVICE
ARG PORT
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -r nova && useradd -r -g nova nova \
    && mkdir -p /app /data && chown -R nova:nova /app /data
WORKDIR /app
COPY --from=build --chown=nova:nova /workspace/${SERVICE}/target/${SERVICE}-1.0.0.jar /app/app.jar
ENV DATA_DIR=/data
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65.0 -Xmx384m -XX:+ExitOnOutOfMemoryError"
EXPOSE ${PORT}
USER nova
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
