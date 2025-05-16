FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY build/libs/handali-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-Dspring.profiles.active=default", "-jar", "app.jar"]
