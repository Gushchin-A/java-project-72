FROM gradle:8.12.1-jdk21

WORKDIR /app

COPY app .

RUN ./gradlew installDist

CMD ./build/install/app/bin/app