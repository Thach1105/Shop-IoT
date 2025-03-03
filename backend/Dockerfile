FROM openjdk:21

ARG FILE_JAR=target/*.jar

ADD ${FILE_JAR} shopiot-backend.jar

ENTRYPOINT ["java", "-jar", "shopiot-backend.jar"]

EXPOSE 8080