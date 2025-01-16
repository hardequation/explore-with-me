FROM openjdk:21
COPY target/server-0.0.1-SNAPSHOT..jar /app/app2.jar
WORKDIR /app
ENTRYPOINT ["java","-jar","app2.jar"]