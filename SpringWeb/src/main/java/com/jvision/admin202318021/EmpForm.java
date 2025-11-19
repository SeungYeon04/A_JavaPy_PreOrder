package com.jvision.admin202318021;

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
public class EmpForm {

    private Long id;
    private String name;
    private String tel;
    private String address;
    private String intro;

    public Emp toEntity() {
        return new Emp(id, name, tel, address, intro);
    }
}
