FROM openjdk:17-jdk-slim

RUN mkdir /usr/local/apps
WORKDIR /usr/local/apps
RUN mkdir logs

VOLUME logs

ADD maven/pdf-ebook-converter-0.0.8-SNAPSHOT.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "-Dspring.profiles.active=production", "app.jar"]
