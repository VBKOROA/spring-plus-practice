# =============================================
# 1. Build Stage - 애플리케이션 빌드
# =============================================
FROM gradle:8.14.3-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle build --no-daemon


# =============================================
# 2. Final Stage - 최종 이미지 생성
# =============================================
FROM alpine:3.19

# Alpine Linux에 OpenJDK 17 (JRE) 설치
# --no-cache 옵션으로 불필요한 캐시 파일을 남기지 않습니다.
RUN apk --no-cache add openjdk17-jre

# 애플리케이션을 실행할 작업 디렉토리 설정
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 실행 포트 노출 (선택 사항)
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

# 컨테이너가 시작될 때 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# 새로운 릴리즈를 위한 주석 ㅋㅋㅋ