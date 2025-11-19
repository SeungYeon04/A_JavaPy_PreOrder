package com.jvision.admin202318021;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Idea {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String content;

    @Column(nullable = false)
    private String category; // 정책, 기술

    @Column(nullable = false)
    private Long likes = 0L;

    @Column(nullable = false)
    private Long dislikes = 0L;
}

