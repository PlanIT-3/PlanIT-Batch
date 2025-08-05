# 1. 공식 Tomcat 이미지 기반 (버전은 필요에 맞게 조정)
FROM tomcat:9.0-jdk17

# 2. 기존 webapps 디렉토리 제거 (불필요한 기본 앱 제거)
RUN rm -rf /usr/local/tomcat/webapps/*

# 3. WAR 파일 복사
COPY build/libs/PlanIT-Batch-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# 4. 포트 설정
EXPOSE 8080

# 5. 기본 실행은 ENTRYPOINT로 유지