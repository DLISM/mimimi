FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /
COPY /src /src
COPY pom.xml /
RUN mvn clean package

FROM openjdk:17-jdk-slim
WORKDIR /
COPY --from=build /target/*.jar mimimi.jar
COPY /uploads uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "mimimi.jar"]