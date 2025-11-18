# 환경 : 쓰레기 순환 정책 제안 플랫폼

기업 중심의 플라스틱 재활용 정책을 제안하고 순환 경제 생태계를 구축하는 플랫폼입니다.

## 프로젝트 개요

- **주제**: 쓰레기 순환 정책 제안 플랫폼 (기업 중심)
- **기술 스택**: 
  - 백엔드: Spring Boot 3.5.5, Java 21
  - 프론트엔드: Thymeleaf, Bootstrap 5, Chart.js
  - 데이터 분석: Python (Flask), Pandas, NumPy
  - 데이터베이스: H2 Database (개발), MySQL (운영)
  - 인증: OAuth2 (Google, Naver)

## 주요 기능

### 1. 정책 제안 게시판 (CRUD)
- ✅ 게시글 작성, 조회, 수정, 삭제
- ✅ 카테고리별 분류 (재활용 제조 공장 지원, 재활용 수입 공장 지원, 폐기물 재활용 공장 지원, 순환 경제 생태계)
- ✅ 검색 기능 (제목, 내용)
- ✅ 조회수 기능
- ✅ 페이징 처리

### 2. 소셜 로그인
- ✅ Google OAuth2 로그인
- ✅ Naver OAuth2 로그인
- ✅ 로그인한 사용자만 게시글 작성/수정/삭제 가능

### 3. 데이터 분석 및 시각화
- ✅ 파이썬 기반 데이터 분석 서비스
- ✅ 플라스틱 재활용률 추이 분석 (재활용률, 재활용 플라스틱 사용량, 재활용 공장 수 등)
- ✅ 카테고리별 통계 시각화
- ✅ Chart.js를 활용한 대시보드

## 프로젝트 구조

```
startproject/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/jvision/admin202318021/
│       │       ├── entity/          # 엔티티 클래스
│       │       ├── repository/       # 데이터 접근 계층
│       │       ├── service/          # 비즈니스 로직
│       │       ├── controller/       # 컨트롤러
│       │       ├── config/           # 설정 클래스
│       │       └── dto/              # 데이터 전송 객체
│       └── resources/
│           ├── templates/            # Thymeleaf 템플릿
│           ├── static/               # 정적 리소스
│           └── application.properties
├── python_service/                   # 파이썬 데이터 분석 서비스
│   ├── app.py                       # Flask 애플리케이션
│   ├── data_analysis.py             # 데이터 분석 모듈
│   └── requirements.txt             # 파이썬 의존성
├── build.gradle                     # Gradle 빌드 설정
└── README.md                        # 프로젝트 문서
```

## 개발환경 구축

### 필수 요구사항
- Java 21 이상
- Python 3.8 이상
- Gradle 7.x 이상 (또는 Gradle Wrapper 사용)

### 1. Java/Spring Boot 환경 설정

#### 1.1 프로젝트 클론 및 이동
```bash
cd startproject
```

#### 1.2 Gradle 빌드
```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

#### 1.3 OAuth2 설정

`src/main/resources/application.properties` 파일을 열어 OAuth2 클라이언트 정보를 설정합니다.

**Google OAuth2 설정:**
1. [Google Cloud Console](https://console.cloud.google.com/) 접속
2. 프로젝트 생성 및 OAuth 2.0 클라이언트 ID 생성
3. 승인된 리디렉션 URI: `http://localhost:8080/login/oauth2/code/google`
4. Client ID와 Client Secret을 `application.properties`에 입력

**Naver OAuth2 설정:**
1. [Naver Developers](https://developers.naver.com/) 접속
2. 애플리케이션 등록
3. 서비스 URL: `http://localhost:8080`
4. Callback URL: `http://localhost:8080/login/oauth2/code/naver`
5. Client ID와 Client Secret을 `application.properties`에 입력

```properties
# application.properties 예시
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET

spring.security.oauth2.client.registration.naver.client-id=YOUR_NAVER_CLIENT_ID
spring.security.oauth2.client.registration.naver.client-secret=YOUR_NAVER_CLIENT_SECRET
```

#### 1.4 애플리케이션 실행
```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

애플리케이션이 `http://localhost:8080`에서 실행됩니다.

### 2. Python 데이터 분석 서비스 설정

#### 2.1 가상환경 생성 (권장)
```bash
cd python_service
python -m venv venv

# Windows
venv\Scripts\activate

# Linux/Mac
source venv/bin/activate
```

#### 2.2 의존성 설치
```bash
pip install -r requirements.txt
```

#### 2.3 서비스 실행
```bash
python app.py
```

서비스가 `http://localhost:5000`에서 실행됩니다.

## 실행 방법

### 전체 시스템 실행 순서

1. **Python 서비스 실행** (터미널 1)
   ```bash
   cd python_service
   python app.py
   ```

2. **Spring Boot 애플리케이션 실행** (터미널 2)
   ```bash
   cd startproject
   gradlew.bat bootRun  # Windows
   # 또는
   ./gradlew bootRun    # Linux/Mac
   ```

3. **브라우저에서 접속**
   - 메인 페이지: http://localhost:8080
   - 대시보드: http://localhost:8080/dashboard
   - 게시판: http://localhost:8080/posts

## API 엔드포인트

### Spring Boot API
- `GET /api/stats/category` - 카테고리별 통계
- `GET /api/python/analysis` - 파이썬 분석 결과
- `GET /api/python/recycling-data` - 플라스틱 재활용 데이터

### Python Service API
- `GET /api/analysis` - 데이터 분석 결과
- `GET /api/recycling-data` - 플라스틱 재활용 데이터
- `GET /api/health` - 헬스 체크

## 데이터베이스

기본적으로 H2 인메모리 데이터베이스를 사용합니다.
- H2 콘솔: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- 사용자명: `sa`
- 비밀번호: (비어있음)

## 주요 화면

1. **메인 페이지** (`/`)
   - 플랫폼 소개 및 주요 기능 안내

2. **게시판 목록** (`/posts`)
   - 정책 제안 게시글 목록
   - 카테고리별 필터링
   - 검색 기능

3. **게시글 작성/수정** (`/posts/new`, `/posts/{id}/edit`)
   - 로그인 필요
   - 제목, 카테고리, 내용 입력

4. **대시보드** (`/dashboard`)
   - 카테고리별 통계 차트
   - 플라스틱 재활용 데이터 시각화
   - 파이썬 분석 결과 표시

## 기술적 특징

### 백엔드
- Spring Security를 활용한 인증/인가
- OAuth2 Client를 통한 소셜 로그인
- JPA를 활용한 데이터 영속성 관리
- RESTful API 설계

### 프론트엔드
- Thymeleaf 템플릿 엔진
- Bootstrap 5를 활용한 반응형 디자인
- Chart.js를 활용한 데이터 시각화

### 데이터 분석
- Flask 기반 REST API
- Pandas를 활용한 데이터 분석
- NumPy를 활용한 통계 계산

## 문제 해결

### OAuth2 로그인이 작동하지 않는 경우
- `application.properties`의 Client ID와 Secret이 올바른지 확인
- 리디렉션 URI가 OAuth2 제공자에 등록되어 있는지 확인

### Python 서비스 연결 실패
- Python 서비스가 `http://localhost:5000`에서 실행 중인지 확인
- 방화벽 설정 확인

### 데이터베이스 오류
- H2 콘솔에서 데이터베이스 상태 확인
- `application.properties`의 JPA 설정 확인

## 향후 개선 사항

- [ ] 실제 공공 데이터 API 연동
- [ ] 댓글 기능 추가
- [ ] 좋아요/추천 기능
- [ ] 파일 업로드 기능
- [ ] 관리자 페이지
- [ ] 이메일 알림 기능
- [ ] 실시간 통계 업데이트

## 라이선스

이 프로젝트는 교육 목적으로 제작되었습니다.

## 개발자 정보

- 프로젝트명: 플라스틱 재활용 정책 제안 플랫폼
- 개발 기간: 2024
- 목적: 사회 이슈 해결을 위한 정책 제안 플랫폼 구현

## 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Security OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
- [공공 데이터 포털](https://www.data.go.kr/)
- [Chart.js 문서](https://www.chartjs.org/docs/latest/)

