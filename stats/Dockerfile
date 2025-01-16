FROM openjdk:21

COPY client/target/client-0.0.1-SNAPSHOT.jar /app/app1.jar
COPY server/target/server-0.0.1-SNAPSHOT..jar /app/app2.jar

WORKDIR /app

CMD ["java", "-jar", "app1.jar", "&", "java", "-jar", "app2.jar"]