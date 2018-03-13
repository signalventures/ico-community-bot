FROM openjdk:8-jre-alpine
COPY target/ico-community-bot-*.jar /app.jar
CMD ["/usr/bin/java", "-jar", "/app.jar"]