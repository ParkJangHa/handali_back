# Amazon Corretto 17 베이스 이미지 사용
FROM amazoncorretto:17
WORKDIR /app

COPY build/libs/handali-0.0.1-SNAPSHOT.jar app.jar

# spring.profiles.active는 docker-compose에서 넘김
CMD ["java", "-Dspring.profiles.active=${SPRING_PROFILE}", "-jar", "app.jar"]

