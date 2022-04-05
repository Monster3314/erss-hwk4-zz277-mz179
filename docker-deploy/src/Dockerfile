FROM openjdk:17

RUN mkdir /code

WORKDIR /code

ARG JAR_FILE=EM_System-1.0-SNAPSHOT.jar

COPY ${JAR_FILE} EM_System-1.0-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "EM_System-1.0-SNAPSHOT.jar"]
