FROM openjdk:8-jdk-alpine
MAINTAINER ogochi.pl

ARG JAR_FILE=build/libs/rate_classes_server-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} server.jar

ENTRYPOINT ["java","-jar","server.jar"]
