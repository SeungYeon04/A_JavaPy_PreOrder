package com.jvision.admin202318021;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
public class EmpController {
    
    @Autowired
    private EmpRepository empRepository;

    // 루트 화면 - 소개페이지
    @GetMapping("/")
    public String index() {
        return "intro";
    }

    // 커뮤니티 게시판
    @GetMapping("/community")
    public String community(Model model) {
        List<Emp> empEntity = (List<Emp>) empRepository.findAll();
        model.addAttribute("resultList", empEntity);
        return "community";
    }

    // 등록 폼 화면
    @GetMapping("/emp")
    public String empPage(Model model) {
        model.addAttribute("empForm", new EmpForm());
        return "emp";
    }

    // 등록
    @PostMapping("/emp/register")
    public String createArticle(EmpForm form) {
        log.info(form.toString());

        Emp emp = form.toEntity();
        log.info(emp.toString());

        Emp save = empRepository.save(emp); //H2 DB에 저장
        log.info(save.toString());

        return "redirect:/community";
    }

    // 수정 폼 보여주기
    @GetMapping("/emp/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Emp empEntity = empRepository.findById(id).orElse(null);
        if (empEntity != null) {
            EmpForm form = new EmpForm(empEntity.getId(), empEntity.getName(), 
                                      empEntity.getTel(), empEntity.getAddress(), empEntity.getIntro());
            model.addAttribute("empForm", form);
        }
        return "emp";
    }

    // 수정 처리
    @PostMapping("/emp/update")
    public String updateArticle(EmpForm form) {
        log.info("수정 데이터: " + form.toString());

        if (form.getId() != null) {
            Emp emp = empRepository.findById(form.getId()).orElse(null);
            if (emp != null) {
                emp.setName(form.getName());
                emp.setTel(form.getTel());
                emp.setAddress(form.getAddress());
                emp.setIntro(form.getIntro());
                empRepository.save(emp);
                log.info("수정 완료: " + emp.toString());
            }
        }

        return "redirect:/community";
    }

    // 삭제
    @PostMapping("/emp/delete/{id}")
    public String deleteArticle(@PathVariable Long id) {
        log.info("삭제 id: " + id);
        empRepository.deleteById(id);
        return "redirect:/community";
    }

    // 전체 삭제
    @PostMapping("/emp/deleteAll")
    public String deleteAll() {
        log.info("전체 삭제");
        empRepository.deleteAll();
        return "redirect:/community";
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

    // API 환경데이터 시각화
    @GetMapping("/data")
    public String data() {
        return "data";
    }

    // 아이디어 투표
    @GetMapping("/idea")
    public String idea() {
        return "idea";
    }

}
