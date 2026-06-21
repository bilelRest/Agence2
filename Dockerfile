FROM openjdk:17

WORKDIR /app

COPY src ./src
COPY target//agence-0.0.1-SNAPSHOT.jar /app/agence-0.0.1-SNAPSHOT.jar
COPY src/main/resources/application.properties /app/src/main/resources/application.properties


EXPOSE 6060


CMD ["java", "-jar", "agence-0.0.1-SNAPSHOT.jar"]
