package com.jvision.admin202318021;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.SpringApplication;

// Emp Form 이랑 같은 상속
@AllArgsConstructor
@ToString
//생성자?자동생성
@NoArgsConstructor
@Entity
public class Emp {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;
    @Column
    private String dept;
    @Column
    private String position;
    @Column
    private String tel;
    @Column
    private String email;
}
