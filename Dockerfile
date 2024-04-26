FROM ghcr.io/graalvm/native-image-community:22
WORKDIR /opt/demo
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY ./gradle ./gradle
COPY ./src ./src
RUN microdnf install findutils
RUN ./gradlew --no-daemon clean nativeCompile

FROM ubuntu:22.04
COPY --from=0 ./opt/demo/build/native/nativeCompile/rbmon2 /app/rbmon2
ENTRYPOINT ["/app/rbmon2"]
