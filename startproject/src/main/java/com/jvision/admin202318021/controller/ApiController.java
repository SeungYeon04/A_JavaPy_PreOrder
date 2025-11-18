package com.jvision.admin202318021.controller;

import com.jvision.admin202318021.repository.PolicyPostRepository;
import com.jvision.admin202318021.service.PythonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    
    private final PolicyPostRepository policyPostRepository;
    private final PythonService pythonService;
    
    @GetMapping("/stats/category")
    public Map<String, Object> getCategoryStats() {
        List<Object[]> stats = policyPostRepository.countByCategory();
        Map<String, Object> result = new HashMap<>();
        Map<String, Long> categoryCount = new HashMap<>();
        
        for (Object[] stat : stats) {
            categoryCount.put((String) stat[0], (Long) stat[1]);
        }
        
        result.put("categories", categoryCount);
        return result;
    }
    
    @GetMapping("/python/analysis")
    public Map<String, Object> getPythonAnalysis() {
        return pythonService.getAnalysis();
    }
    
    @GetMapping("/python/recycling-data")
    public Map<String, Object> getRecyclingData() {
        return pythonService.getRecyclingData();
    }
}

