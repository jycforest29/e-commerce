## DockerFile : docker build를 통해 이미지 생성 가능
## Docker Build : DockerFile 및 컨텍스트에서 이미지를 빌드하는 도커 명령

# 11-jre-slim-buster 는 도커에서 공식적으로 지원하는 슬림 버전의 라이브러리는 아니라는 점 참고
FROM openjdk:11-jre-slim-buster
# COPY [컨테이너외부] [컨테이너내부]
COPY ./build/libs/commerce-0.0.1-SNAPSHOT.jar /build/libs/commerce-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar","/build/libs/commerce-0.0.1-SNAPSHOT.jar"]