FROM openjdk:17.0.2-jdk-slim-buster

WORKDIR /opt/app
ARG JAR_FILE=target/*.jar
ARG JAVA_OPTS="-XX:+UseContainerSupport"
COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar " + $JAVA_OPTS,"app.jar"]
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar