# Python 데이터 분석 서비스

건강 관련 공공 데이터를 분석하는 Flask 기반 REST API 서비스입니다.

## 기능

- 건강 지표 추이 데이터 분석 (기대수명, 건강수명, 의료비 지출 등)
- 통계 계산 및 예측
- REST API를 통한 데이터 제공

## 설치 및 실행

### 1. 가상환경 생성 (권장)
```bash
python -m venv venv

# Windows
venv\Scripts\activate

# Linux/Mac
source venv/bin/activate
```

### 2. 의존성 설치
```bash
pip install -r requirements.txt
```

### 3. 서비스 실행
```bash
python app.py
```

서비스가 `http://localhost:5000`에서 실행됩니다.

## API 엔드포인트

### GET /api/analysis
데이터 분석 결과를 반환합니다.

**응답 예시:**
```json
{
  "summary": "현재 평균 기대수명은 83.9세, 건강수명은 67.9세입니다. 건강수명 비율은 80.9%입니다.",
  "life_expectancy": 83.9,
  "healthy_life_expectancy": 67.9,
  "health_ratio": 80.9,
  "predicted_life_2025": 84.1,
  "predicted_healthy_2025": 68.2,
  "trend_data": {
    "years": [2015, 2016, ...],
    "life_expectancy": [82.1, 82.3, ...],
    "healthy_life_expectancy": [65.2, 65.5, ...],
    "medical_expenditure": [3.2, 3.4, ...]
  }
}
```

### GET /api/health-data
건강 관련 원시 데이터를 반환합니다.

### GET /api/health
서비스 상태를 확인합니다.

## 공공 데이터 API 연동

실제 공공 데이터를 사용하려면 `data_analysis.py`의 `fetch_public_data` 메서드를 수정하여 공공 데이터 포털 API를 호출하도록 설정하세요.

1. [공공 데이터 포털](https://www.data.go.kr/)에서 API 키 발급
2. `data_analysis.py`에 API 키 설정
3. 해당 API의 엔드포인트 및 데이터 형식에 맞게 코드 수정

## 개발 참고사항

- 현재는 샘플 데이터를 사용합니다.
- 실제 운영 시에는 공공 데이터 API를 연동해야 합니다.
- 데이터 분석 로직은 `data_analysis.py`에 구현되어 있습니다.

