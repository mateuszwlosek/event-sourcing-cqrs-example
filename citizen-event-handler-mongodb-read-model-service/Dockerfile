FROM maven:3.8.5-openjdk-17-slim

WORKDIR /app

ADD . /app
RUN mvn clean package -Ddir=app
RUN mv target/citizen-event-handler-mongodb-read-model-service-1.0.0.jar citizen-event-handler-mongodb-read-model-service-1.0.0.jar

ENTRYPOINT java $JAVA_OPTS -jar citizen-event-handler-mongodb-read-model-service-1.0.0.jar
