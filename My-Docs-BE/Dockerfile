# 1단계: 빌드 스테이지
FROM gradle:8.12-jdk21 AS build
WORKDIR /app

# 그래들 설정 파일 복사
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# 소스 코드 복사 및 빌드 (테스트 제외)
COPY src ./src
RUN gradle build -x test --no-daemon

# 2단계: 실행 스테이지
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 빌드 결과물만 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 실행 포트 설정
EXPOSE 8080

# 컨테이너 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
