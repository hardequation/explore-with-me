FROM openjdk:21
COPY target/ewm-0.0.1-SNAPSHOT.jar /app/app1.jar
WORKDIR /app
ENTRYPOINT ["java","-jar","app1.jar"]