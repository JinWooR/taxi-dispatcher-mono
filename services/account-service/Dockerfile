# 빌드 단계
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app

# Gradle 빌드
COPY . .
RUN chmod +x gradlew && ./gradlew build -x test --no-daemon

# 런타임 단계
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/services/account-service/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 환경 설정 (docker-compose에서 오버라이드 가능)
ENV SPRING_PROFILES_ACTIVE=dev

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
