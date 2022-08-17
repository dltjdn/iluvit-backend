FROM openjdk:11-jre-slim
ARG JAR_FILE=build/libs/iLUVit-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} iluvit_server.jar
ENTRYPOINT ["java","-jar","/iluvit_server.jar"]