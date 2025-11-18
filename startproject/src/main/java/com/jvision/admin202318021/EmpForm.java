package com.jvision.admin202318021;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class EmpForm {

    private String name;
    private String dept;
    private String position;
    private String tel;
    private String email;

    public Emp toEntity() {
        return new Emp(null, name, dept, position, tel, email);
    }
}
