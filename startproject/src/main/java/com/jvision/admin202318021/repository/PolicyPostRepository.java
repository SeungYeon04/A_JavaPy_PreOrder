package com.jvision.admin202318021.repository;

import com.jvision.admin202318021.entity.PolicyPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PolicyPostRepository extends JpaRepository<PolicyPost, Long> {
    
    Page<PolicyPost> findByCategory(String category, Pageable pageable);
    
    Page<PolicyPost> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    
    @Query("SELECT p.category, COUNT(p) FROM PolicyPost p GROUP BY p.category")
    List<Object[]> countByCategory();
}

