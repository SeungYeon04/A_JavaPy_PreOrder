package com.jvision.admin202318021.controller;

import com.jvision.admin202318021.repository.PolicyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    
    private final PolicyPostRepository policyPostRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 카테고리별 게시글 수 통계
        List<Object[]> categoryStats = policyPostRepository.countByCategory();
        model.addAttribute("categoryStats", categoryStats);
        
        return "dashboard/index";
    }
}

