# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app

# 소스 코드 복사
COPY . .

# Gradle 빌드 (테스트 제외)
RUN chmod +x ./gradlew && \
    ./gradlew :services:customer-service:build --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/services/customer-service/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
