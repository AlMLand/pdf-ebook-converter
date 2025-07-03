FROM openjdk:17-jdk-slim

RUN mkdir /usr/local/apps
WORKDIR /usr/local/apps
RUN mkdir logs

VOLUME logs

ADD maven/pdf-ebook-converter-0.0.13-SNAPSHOT.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "-Xms400m", "-Xmx400m", "app.jar"]
