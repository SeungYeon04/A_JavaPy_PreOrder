package com.jvision.admin202318021;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataAnalysisService {

    private final Path uploadDir = Path.of("uploads");
    // static 폴더 하위에 분석결과를 두어 Spring이 정적 리소스로 제공하도록 설정
    private final Path outputDir = Path.of("src", "main", "resources", "static", "analysis_outputs");

    @Getter
    private String lastStatus = "READY";

    public void setLastStatus(String status) {
        this.lastStatus = status;
    }

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
    // prefer venv python if present (python/.venv/Scripts/python.exe)
    String venvPython = Path.of("python", ".venv", "Scripts", "python.exe").toString();
    String python = Files.exists(Path.of(venvPython)) ? venvPython : "python";

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
    // 웹 템플릿/프론트에서 사용할 수 있도록 정적 리소스 경로를 반환
    return Map.of(
        "region", "/analysis_outputs/chart_region.png",
        "material", "/analysis_outputs/chart_material.png",
        "trend", "/analysis_outputs/chart_trend.png"
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

    public List<String> listOutputFiles() {
        try {
            if (!Files.exists(outputDir)) return List.of();
            return Files.list(outputDir)
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .toList();
        } catch (IOException e) {
            log.error("output folder read error", e);
            return List.of();
        }
    }

    /**
     * Run the repository's Python analysis script (used by the web "Regenerate" button).
     * Tries to use conda (if available) to run inside the 'recycle' env, otherwise falls back to
     * whatever 'python' is on PATH.
     */
    public void runAnalysis() throws Exception {
        ensureDirs();
        Path script = Path.of("python", "analyze_csv_outputs.py");
        if (!Files.exists(script)) {
            throw new IOException("분석 스크립트를 찾을 수 없습니다: " + script.toString());
        }

        // Try conda run first (user environment path). Adjust path if needed for other machines.
        String conda = System.getenv("CONDA_EXE");
        ProcessBuilder pb;
        if (conda != null && Files.exists(Path.of(conda))) {
            pb = new ProcessBuilder(conda, "run", "-n", "recycle", "python", script.toString());
        } else {
            // fallback to plain python (may fail if dependencies are missing)
            String python = "python";
            pb = new ProcessBuilder(python, script.toString());
        }
        pb.redirectErrorStream(true);
        // set working directory to project root so relative paths in the script resolve
        pb.directory(Path.of(".").toFile());
        Process p = pb.start();
        // capture output to logs
        try (var is = p.getInputStream(); var br = new java.io.BufferedReader(new java.io.InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                log.info(line);
            }
        }
        int code = p.waitFor();
        if (code != 0) {
            throw new RuntimeException("Python 분석 실패, exit=" + code);
        }
    }
}

