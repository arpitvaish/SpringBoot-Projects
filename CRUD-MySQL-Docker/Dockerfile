FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8080
ADD ./build/libs/employee-registration-0.1.0-SNAPSHOT.jar employee-registration-0.1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","employee-registration-0.1.0-SNAPSHOT.jar"]