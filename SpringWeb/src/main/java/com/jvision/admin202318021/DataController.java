package com.jvision.admin202318021;

import lombok.RequiredArgsConstructor;
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

import java.nio.file.Path;

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
        return "data";
    }

    @PostMapping(value = "/data/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestParam("file") MultipartFile master,
                         @RequestParam(value = "summary", required = false) MultipartFile summary,
                         @RequestParam(value = "facilities", required = false) MultipartFile facilities,
                         @RequestParam(value = "mapping", required = false) MultipartFile mapping) throws Exception {
        dataAnalysisService.handleUpload(master, summary, facilities, mapping);
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

