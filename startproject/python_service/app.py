from flask import Flask, jsonify
from flask_cors import CORS
import pandas as pd
import numpy as np
from datetime import datetime
import json

app = Flask(__name__)
CORS(app)  # CORS 허용

# 플라스틱 재활용 관련 샘플 데이터 (실제로는 공공 데이터 API에서 가져옴)
def get_recycling_data():
    """플라스틱 재활용 관련 데이터 생성 (실제로는 공공 데이터 API 사용)"""
    years = list(range(2015, 2025))
    recycling_rate = [34.2, 35.8, 37.5, 39.1, 41.2, 43.5, 45.8, 48.2, 50.5, 52.8]  # 재활용률 (%)
    recycled_plastic_usage = [85, 92, 98, 105, 112, 120, 128, 135, 142, 150]  # 재활용 플라스틱 사용량 (만톤)
    recycling_factories = [120, 135, 150, 168, 185, 202, 220, 238, 255, 275]  # 재활용 공장 수
    waste_plastic_collection = [250, 265, 280, 295, 310, 325, 340, 355, 370, 385]  # 폐플라스틱 수거량 (만톤)
    
    data = {
        'year': years,
        'recycling_rate': recycling_rate,
        'recycled_plastic_usage': recycled_plastic_usage,
        'recycling_factories': recycling_factories,
        'waste_plastic_collection': waste_plastic_collection
    }
    return pd.DataFrame(data)

def analyze_recycling_trend():
    """플라스틱 재활용 추이 분석"""
    df = get_recycling_data()
    
    # 평균 증가율 계산
    avg_recycling_increase = np.mean(np.diff(df['recycling_rate']))
    avg_usage_increase = np.mean(np.diff(df['recycled_plastic_usage']))
    
    # 예측 (2025년)
    predicted_recycling_2025 = df['recycling_rate'].iloc[-1] + avg_recycling_increase
    predicted_usage_2025 = df['recycled_plastic_usage'].iloc[-1] + avg_usage_increase
    
    # 재활용 효율 (수거량 대비 재활용 사용량)
    efficiency = (df['recycled_plastic_usage'].iloc[-1] / df['waste_plastic_collection'].iloc[-1]) * 100
    
    analysis = {
        'recycling_rate': float(df['recycling_rate'].iloc[-1]),
        'recycled_plastic_usage': float(df['recycled_plastic_usage'].iloc[-1]),
        'recycling_factories': float(df['recycling_factories'].iloc[-1]),
        'efficiency': float(efficiency),
        'predicted_recycling_2025': float(predicted_recycling_2025),
        'predicted_usage_2025': float(predicted_usage_2025),
        'trend': 'improving',
        'summary': f"현재 플라스틱 재활용률은 {df['recycling_rate'].iloc[-1]}%이며, 재활용 플라스틱 사용량은 {df['recycled_plastic_usage'].iloc[-1]}만톤입니다. 재활용 공장은 {df['recycling_factories'].iloc[-1]}개가 운영 중입니다."
    }
    
    return analysis

def get_category_statistics():
    """카테고리별 통계"""
    categories = {
        '재활용제조공장지원': 45,
        '재활용수입공장지원': 32,
        '폐기물재활용공장지원': 38,
        '순환경제생태계': 25
    }
    return categories

@app.route('/api/analysis', methods=['GET'])
def get_analysis():
    """데이터 분석 결과 반환"""
    try:
        analysis = analyze_recycling_trend()
        df = get_recycling_data()
        
        result = {
            'summary': analysis['summary'],
            'recycling_rate': analysis['recycling_rate'],
            'recycled_plastic_usage': analysis['recycled_plastic_usage'],
            'recycling_factories': analysis['recycling_factories'],
            'efficiency': analysis['efficiency'],
            'predicted_recycling_2025': analysis['predicted_recycling_2025'],
            'predicted_usage_2025': analysis['predicted_usage_2025'],
            'trend_data': {
                'years': df['year'].tolist(),
                'recycling_rate': df['recycling_rate'].tolist(),
                'recycled_plastic_usage': df['recycled_plastic_usage'].tolist(),
                'recycling_factories': df['recycling_factories'].tolist(),
                'waste_plastic_collection': df['waste_plastic_collection'].tolist()
            },
            'timestamp': datetime.now().isoformat()
        }
        
        return jsonify(result)
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/recycling-data', methods=['GET'])
def get_recycling_data_endpoint():
    """플라스틱 재활용 데이터 반환"""
    try:
        df = get_recycling_data()
        return jsonify({
            'data': df.to_dict('records'),
            'columns': df.columns.tolist()
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/health', methods=['GET'])
def health_check():
    """헬스 체크"""
    return jsonify({'status': 'healthy', 'service': 'recycling-data-analysis'})

if __name__ == '__main__':
    print("플라스틱 재활용 데이터 분석 서비스 시작...")
    print("서비스 URL: http://localhost:5000")
    app.run(host='0.0.0.0', port=5000, debug=True)

