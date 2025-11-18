# 빠른 시작 가이드

## 1단계: Java 환경 확인
```bash
java -version  # Java 21 이상 필요
```

## 2단계: Python 환경 확인
```bash
python --version  # Python 3.8 이상 필요
```

## 3단계: OAuth2 설정

### Google OAuth2
1. https://console.cloud.google.com/ 접속
2. 새 프로젝트 생성
3. "API 및 서비스" > "사용자 인증 정보" 이동
4. "OAuth 2.0 클라이언트 ID" 생성
5. 승인된 리디렉션 URI 추가: `http://localhost:8080/login/oauth2/code/google`
6. Client ID와 Secret을 `application.properties`에 입력

### Naver OAuth2
1. https://developers.naver.com/ 접속
2. "애플리케이션 등록"
3. 서비스 URL: `http://localhost:8080`
4. Callback URL: `http://localhost:8080/login/oauth2/code/naver`
5. Client ID와 Secret을 `application.properties`에 입력

## 4단계: 프로젝트 실행

### 터미널 1: Python 서비스
```bash
cd python_service
python -m venv venv
venv\Scripts\activate  # Windows
# 또는 source venv/bin/activate  # Linux/Mac
pip install -r requirements.txt
python app.py
```

### 터미널 2: Spring Boot
```bash
cd startproject
gradlew.bat bootRun  # Windows
# 또는 ./gradlew bootRun  # Linux/Mac
```

## 5단계: 브라우저 접속
- http://localhost:8080

## 문제 해결

### 포트 충돌
- Spring Boot: `application.properties`에서 `server.port` 변경
- Python: `app.py`에서 `port=5000` 변경

### OAuth2 오류
- 리디렉션 URI가 정확히 일치하는지 확인
- Client ID/Secret이 올바른지 확인

### Python 서비스 연결 실패
- Python 서비스가 실행 중인지 확인: http://localhost:5000/api/health
- 방화벽 설정 확인

