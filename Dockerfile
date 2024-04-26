FROM ubuntu:22.04
COPY ./build/native/nativeCompile/rbmon2 /app/rbmon2
ENTRYPOINT ["/app/rbmon2"]
