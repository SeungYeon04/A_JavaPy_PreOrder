package com.jvision.admin202318021;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class EmpController {
    // 이거 슬래시 해야하네
    @GetMapping("/emp")
    public String newEmpForm() {
        //머스태치 찾아옴 classpath:/templates/emp/new.mustache
        return "emp";
    }

    @Autowired
    private EmpRepository empRepository;

    @PostMapping("/emp/register")
    public String createArticle(EmpForm form) {
        //System.out.println("입력된 데이터: " + form.toString());
        log.info(form.toString());

        Emp emp = form.toEntity();
        log.info(emp.toString());

        Emp save = empRepository.save(emp); //H2 DB에 저장
        log.info(save.toString());

        return "";
    }

    // 단일 데이터 조회용
    @GetMapping("/emp/{id}")
    public String show(@PathVariable Long id, Model model) {
        //http://localhost:8080/emp/1 이렇게 검색하면 됨.
        log.info("id: "+ id);

        // private empRepository 로 담아온 거 사용
        //Optional<Emp> empEntity = empRepository.findById(id); -> 이런 것도 된다. (근데 머스테치에 뭐 추가해야 함)
        Emp empEntity = empRepository.findById(id).orElse(null); //조건 null 허용

        model.addAttribute("resultmd", empEntity);

        return "result/showmd";
    }

    // 데이터 목록 조회용
    @GetMapping("/emp/list")
    public String index(Model model) {

        List<Emp> empEntity = (List<Emp>) empRepository.findAll();

        model.addAttribute("resultList", empEntity);

        return "result/index";
    }
}
