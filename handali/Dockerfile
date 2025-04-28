# Amazon Corretto 17 베이스 이미지 사용
FROM amazoncorretto:17

# JAR 파일을 컨테이너로 복사
COPY build/libs/handali-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너가 실행될 때 실행할 명령
ENTRYPOINT ["java", "-jar", "/app.jar"]
