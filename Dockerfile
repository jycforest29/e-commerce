## DockerFile : docker build를 통해 이미지 생성 가능
## Docker Build : DockerFile 및 컨텍스트에서 이미지를 빌드하는 도커 명령
FROM openjdk:11
ARG JAR_FILE=/build/libs/commerce-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]