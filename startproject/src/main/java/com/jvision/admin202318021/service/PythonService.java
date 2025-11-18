package com.jvision.admin202318021.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PythonService {
    
    private static final String PYTHON_SERVICE_URL = "http://localhost:5000";
    private final RestTemplate restTemplate;
    
    public Map<String, Object> getAnalysis() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                PYTHON_SERVICE_URL + "/api/analysis", Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("파이썬 서비스 연결 실패: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "파이썬 분석 서비스에 연결할 수 없습니다.");
            errorResponse.put("summary", "서비스를 실행해주세요: python python_service/app.py");
            return errorResponse;
        }
    }
    
    public Map<String, Object> getRecyclingData() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                PYTHON_SERVICE_URL + "/api/recycling-data", Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("파이썬 서비스 연결 실패: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}

