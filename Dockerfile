FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/GateWay-0.0.1-SNAPSHOT.jar GateWay-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "GateWay-0.0.1-SNAPSHOT.jar"]
