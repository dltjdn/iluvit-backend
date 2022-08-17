FROM openjdk:11-jdk
ARG IDLE_PROFILE
ARG JAR_FILE=build/libs/*.jar
ENV ENV_IDLE_PROFILE=$IDLE_PROFILE
COPY ${JAR_FILE} /iluvit.jar
RUN echo $ENV_IDLE_PROFILE
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${ENV_IDLE_PROFILE}", "/iluvit.jar"]