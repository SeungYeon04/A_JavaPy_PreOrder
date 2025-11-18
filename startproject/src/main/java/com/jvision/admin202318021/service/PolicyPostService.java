package com.jvision.admin202318021.service;

import com.jvision.admin202318021.dto.PolicyPostDto;
import com.jvision.admin202318021.entity.PolicyPost;
import com.jvision.admin202318021.repository.PolicyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PolicyPostService {
    
    private final PolicyPostRepository policyPostRepository;
    
    @Transactional
    public PolicyPost createPost(PolicyPostDto dto) {
        PolicyPost post = new PolicyPost();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCategory(dto.getCategory());
        post.setAuthor(dto.getAuthor());
        post.setAuthorEmail(dto.getAuthorEmail());
        post.setViewCount(0);
        return policyPostRepository.save(post);
    }
    
    @Transactional(readOnly = true)
    public Page<PolicyPost> getAllPosts(Pageable pageable) {
        return policyPostRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<PolicyPost> getPostsByCategory(String category, Pageable pageable) {
        return policyPostRepository.findByCategory(category, pageable);
    }
    
    @Transactional(readOnly = true)
    public PolicyPost getPostById(Long id) {
        return policyPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }
    
    @Transactional
    public PolicyPost updatePost(Long id, PolicyPostDto dto) {
        PolicyPost post = getPostById(id);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCategory(dto.getCategory());
        return policyPostRepository.save(post);
    }
    
    @Transactional
    public void deletePost(Long id) {
        policyPostRepository.deleteById(id);
    }
    
    @Transactional
    public void incrementViewCount(Long id) {
        PolicyPost post = getPostById(id);
        post.setViewCount(post.getViewCount() + 1);
        policyPostRepository.save(post);
    }
    
    @Transactional(readOnly = true)
    public Page<PolicyPost> searchPosts(String keyword, Pageable pageable) {
        return policyPostRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }
}

