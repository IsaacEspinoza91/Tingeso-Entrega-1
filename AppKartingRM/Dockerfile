FROM openjdk:17
ARG JAR_FILE=./build/libs/AppKartingRM-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} AppKartingRM-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./AppKartingRM-backend.jar"]