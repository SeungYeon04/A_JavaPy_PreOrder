package com.jvision.admin202318021;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataAnalysisService {

    private final Path uploadDir = Path.of("uploads");
    private final Path outputDir = Path.of("outputs");

    @Getter
    private String lastStatus = "READY";

    public void handleUpload(MultipartFile master,
                             MultipartFile summary,
                             MultipartFile facilities,
                             MultipartFile mapping) throws Exception {
        ensureDirs();
        Path masterPath = save(master);
        Path summaryPath = summary != null && !summary.isEmpty() ? save(summary) : null;
        Path facilitiesPath = facilities != null && !facilities.isEmpty() ? save(facilities) : null;
        Path mappingPath = mapping != null && !mapping.isEmpty() ? save(mapping) : null;

        lastStatus = "RUNNING";
        runPython(masterPath, summaryPath, facilitiesPath, mappingPath);
        lastStatus = "DONE";
    }

    private void ensureDirs() throws IOException {
        Files.createDirectories(uploadDir);
        Files.createDirectories(outputDir);
    }

    private Path save(MultipartFile file) throws IOException {
        Path dest = uploadDir.resolve(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(dest.toFile())) {
            fos.write(file.getBytes());
        }
        return dest;
    }

    private void runPython(Path master, Path summary, Path facilities, Path mapping) throws Exception {
        String python = "python"; // 필요 시 venv 또는 절대경로로 교체
        ProcessBuilder pb = new ProcessBuilder(
                python, "python/analyze_data.py",
                "--master", master.toString(),
                "--summary", summary != null ? summary.toString() : "",
                "--facilities", facilities != null ? facilities.toString() : "",
                "--mapping", mapping != null ? mapping.toString() : "",
                "--outdir", outputDir.toString()
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        int code = p.waitFor();
        if (code != 0) {
            throw new RuntimeException("Python 분석 실패, exit=" + code);
        }
    }

    public ChartResponse getChartData() {
        return ChartResponse.from(outputDir);
    }

    public Map<String, String> getChartPaths() {
        return Map.of(
                "region", outputDir.resolve("chart_region.png").toString(),
                "material", outputDir.resolve("chart_material.png").toString(),
                "trend", outputDir.resolve("chart_trend.png").toString()
        );
    }

    public String getIndicatorsPath() {
        return outputDir.resolve("indicators.csv").toString();
    }

    public String getGapTablePath() {
        return outputDir.resolve("gap_table.csv").toString();
    }

    public String getGapListPath() {
        return outputDir.resolve("top_gap_list.txt").toString();
    }

    public Path resolveOutput(String name) {
        Path p = outputDir.resolve(name);
        return Files.exists(p) ? p : null;
    }
}

