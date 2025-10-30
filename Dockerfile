FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY build/libs/NotesApplication-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
