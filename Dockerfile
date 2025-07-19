FROM eclipse-temurin:21-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/AutomatedTradingSystem-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]