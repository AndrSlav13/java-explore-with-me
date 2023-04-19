FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar stats-gateway.jar
ENTRYPOINT ["java","-jar","/stats-gateway.jar"]