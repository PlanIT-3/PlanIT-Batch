# 1. 공식 Tomcat 이미지 기반 (버전은 필요에 맞게 조정)
FROM tomcat:9.0-jdk17

# 2. 시간대 설정 (한국 시간)
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 3. 기존 webapps 디렉토리 제거 (불필요한 기본 앱 제거)
RUN rm -rf /usr/local/tomcat/webapps/*

COPY build/libs/PlanIT-Batch-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080


### self build ###
## 1) Builder 단계: Gradle + JDK17로 WAR 빌드
#FROM gradle:7.6-jdk17 AS builder
#WORKDIR /home/gradle/project
#
## 프로젝트 파일 전체를 복사 (캐시 효율보다 단순 호환성 우선)
#COPY --chown=gradle:gradle . .
#
## 테스트 제외하고 WAR 생성
#RUN gradle clean build -x test --no-daemon
#
## 2) Run 단계: Tomcat에 WAR 배포
#FROM tomcat:9.0-jdk17
#
## 한국 시간대 설정
#ENV TZ=Asia/Seoul
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
# && echo $TZ > /etc/timezone
#
## 기본 웹앱 제거
#RUN rm -rf /usr/local/tomcat/webapps/*
#
## Builder 단계에서 생성된 WAR를 ROOT.war로 복사
## 빌드 결과물 위치가 build/libs/*.war 라는 점에 유의
#COPY --from=builder /home/gradle/project/build/libs/*.war \
#     /usr/local/tomcat/webapps/ROOT.war
#
## 컨테이너 노출 포트
#EXPOSE 8080
#
## Tomcat 기동
#CMD ["catalina.sh", "run"]
