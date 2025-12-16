package com.jvision.admin202318021;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DataController {

    private final DataAnalysisService dataAnalysisService;

    @GetMapping("/data")
    public String dataPage(Model model) {
        model.addAttribute("status", dataAnalysisService.getLastStatus());
        model.addAttribute("charts", dataAnalysisService.getChartPaths());
        model.addAttribute("indicatorsPath", dataAnalysisService.getIndicatorsPath());
        model.addAttribute("gapTablePath", dataAnalysisService.getGapTablePath());
        model.addAttribute("gapListPath", dataAnalysisService.getGapListPath());
        List<String> files = dataAnalysisService.listOutputFiles();
        List<Map<String, Object>> outputs = files.stream().map(name -> {
            boolean isImage = name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".gif");
            Map<String, Object> m = new HashMap<>();
            m.put("name", name);
            // human friendly title
            String title = deriveTitleFromFilename(name);
            m.put("title", title);
            m.put("isImage", isImage);
            return m;
        }).collect(Collectors.toList());
        model.addAttribute("outputs", outputs);
        return "data";
    }

    // generate readable title from filename
    private String deriveTitleFromFilename(String name) {
        if (name == null) return "";
        String n = name.toLowerCase();
    if (n.startsWith("env_item_top10_bar")) return "품목별 배출합계 (Top 10)";
    if (n.startsWith("env_item_top10_pie")) return "품목별 배출 비율 (Top 10)";
        if (n.startsWith("env_operator_top5")) return "사업자별 실적 TOP5";
        if (n.startsWith("env_product_top5")) return "품목별 실적 TOP5";
        if (n.startsWith("jb_boxplot")) return "분리배출 유형별 비율 (박스플롯)";
        if (n.startsWith("jb_top5_bar")) return "재활용 비율 Top5 (구분)";
        if (n.startsWith("jb_bottom5_bar")) return "재활용 비율 하위5 (구분)";
        // fallback: remove extension and underscores
        String s = name.replaceAll("\\.[^.]+$", "").replace('_', ' ');
        return s;
    }

    @PostMapping(value = "/data/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestParam("file") MultipartFile master,
                         @RequestParam(value = "summary", required = false) MultipartFile summary,
                         @RequestParam(value = "facilities", required = false) MultipartFile facilities,
                         @RequestParam(value = "mapping", required = false) MultipartFile mapping) throws Exception {
        dataAnalysisService.handleUpload(master, summary, facilities, mapping);
        return "redirect:/data";
    }

    @PostMapping("/data/regenerate")
    public String regenerate() {
        try {
            dataAnalysisService.setLastStatus("RUNNING");
            dataAnalysisService.runAnalysis();
            dataAnalysisService.setLastStatus("DONE");
        } catch (Exception e) {
            // log and set status
            // DataAnalysisService already logs details; set status for UI
            dataAnalysisService.setLastStatus("ERROR: " + e.getMessage());
        }
        return "redirect:/data";
    }

    @GetMapping("/api/data/charts")
    @ResponseBody
    public ChartResponse charts() {
        return dataAnalysisService.getChartData();
    }

    @GetMapping("/data/download/{name}")
    public ResponseEntity<FileSystemResource> download(@PathVariable String name) {
        Path file = dataAnalysisService.resolveOutput(name);
        if (file == null || !file.toFile().exists()) {
            return ResponseEntity.notFound().build();
        }
        FileSystemResource res = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(res);
    }
}

