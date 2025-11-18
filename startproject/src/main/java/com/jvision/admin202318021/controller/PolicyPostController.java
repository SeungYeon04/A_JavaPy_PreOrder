package com.jvision.admin202318021.controller;

import com.jvision.admin202318021.dto.PolicyPostDto;
import com.jvision.admin202318021.entity.PolicyPost;
import com.jvision.admin202318021.service.PolicyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PolicyPostController {
    
    private final PolicyPostService policyPostService;
    
    @GetMapping
    public String listPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        Page<PolicyPost> posts;
        
        if (keyword != null && !keyword.isEmpty()) {
            posts = policyPostService.searchPosts(keyword, pageable);
        } else if (category != null && !category.isEmpty()) {
            posts = policyPostService.getPostsByCategory(category, pageable);
        } else {
            posts = policyPostService.getAllPosts(pageable);
        }
        
        model.addAttribute("posts", posts);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        return "posts/list";
    }
    
    @GetMapping("/new")
    public String newPostForm(Model model, @AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return "redirect:/login";
        }
        model.addAttribute("post", new PolicyPostDto());
        return "posts/form";
    }
    
    @PostMapping
    public String createPost(@ModelAttribute PolicyPostDto dto, 
                            @AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return "redirect:/login";
        }
        
        String name = oauth2User.getAttribute("name");
        String email = oauth2User.getAttribute("email");
        
        dto.setAuthor(name != null ? name : "익명");
        dto.setAuthorEmail(email != null ? email : "");
        
        policyPostService.createPost(dto);
        return "redirect:/posts";
    }
    
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        PolicyPost post = policyPostService.getPostById(id);
        policyPostService.incrementViewCount(id);
        model.addAttribute("post", post);
        return "posts/view";
    }
    
    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model, 
                               @AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return "redirect:/login";
        }
        
        PolicyPost post = policyPostService.getPostById(id);
        String userEmail = oauth2User.getAttribute("email");
        
        if (!post.getAuthorEmail().equals(userEmail)) {
            return "redirect:/posts/" + id;
        }
        
        PolicyPostDto dto = new PolicyPostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCategory(post.getCategory());
        
        model.addAttribute("post", dto);
        return "posts/form";
    }
    
    @PostMapping("/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute PolicyPostDto dto,
                            @AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return "redirect:/login";
        }
        
        PolicyPost post = policyPostService.getPostById(id);
        String userEmail = oauth2User.getAttribute("email");
        
        if (!post.getAuthorEmail().equals(userEmail)) {
            return "redirect:/posts/" + id;
        }
        
        policyPostService.updatePost(id, dto);
        return "redirect:/posts/" + id;
    }
    
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, @AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return "redirect:/login";
        }
        
        PolicyPost post = policyPostService.getPostById(id);
        String userEmail = oauth2User.getAttribute("email");
        
        if (!post.getAuthorEmail().equals(userEmail)) {
            return "redirect:/posts/" + id;
        }
        
        policyPostService.deletePost(id);
        return "redirect:/posts";
    }
}


