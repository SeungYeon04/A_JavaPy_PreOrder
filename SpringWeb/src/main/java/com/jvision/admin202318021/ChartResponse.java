package com.jvision.admin202318021;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;
import java.util.List;

@Data
@AllArgsConstructor
public class ChartResponse {
    private List<Series> region;
    private List<Series> material;
    private List<Series> trend;

    @Data
    @AllArgsConstructor
    public static class Series {
        private String label;
        private Double value;
    }

    public static ChartResponse from(Path outputDir) {
        // JSON 데이터 파싱 로직은 추후 확장. 현재는 빈 리스트 반환.
        return new ChartResponse(List.of(), List.of(), List.of());
    }
}

