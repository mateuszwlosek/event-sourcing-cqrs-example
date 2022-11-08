FROM maven:3.8.5-openjdk-17-slim

WORKDIR /app

ADD . /app
RUN mvn clean package -Ddir=app
RUN mv target/citizen-query-service-1.0.0.jar citizen-query-service-1.0.0.jar

ENTRYPOINT java $JAVA_OPTS -jar citizen-query-service-1.0.0.jar
