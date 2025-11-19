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
    
    @Autowired
    private IdeaRepository ideaRepository;

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
    public String idea(Model model) {
        List<Idea> ideaList = (List<Idea>) ideaRepository.findAll();
        
        // 데모 데이터가 없으면 생성
        if (ideaList.isEmpty()) {
            Idea demoIdea = new Idea();
            demoIdea.setTitle("재활용을 물건으로 바꾸는 공장과 재활용 물건을 이용한 기업을 지원하자.");
            demoIdea.setContent("재활용을 하여 얻은 페트, 캔, 종이 등의 물질들을 사용할 수 있는 재질, 물건으로 만드는 공장에게 지원을 더욱 활발히 할 필요가 있으며, 소비자들이 재활용 산업이 성장하는 과정을 더욱 알 권리가 필요하다고 생각합니다. 또한 재활용을 활용한 물건을 판매하는 자영업자들에게 초기 지원을 하여 순환의 굴레가 지속될 수 있도록 지원이 필요합니다. 또한 자영업자들이 재활용 재질을 사용해야할 이유와 메리트를 제공해야합니다. 그러므로 재활용을 한 물건의 가격을 낮추고 퀄리티를 높이는 기술과 방법을 고민해야하고 그 과정 속 많은 홍보가 필요합니다. 또한 재활용을 활용하면 더욱 이득이 될 수 있도록 해야합니다.");
            demoIdea.setCategory("정책");
            demoIdea.setLikes(15L);
            demoIdea.setDislikes(3L);
            ideaRepository.save(demoIdea);

            Idea demoIdea2 = new Idea();
            demoIdea2.setTitle("재활용 재질을 이용해 조립식 집 개발을 하자");
            demoIdea2.setContent("조립식 집 개발하고 가구도 지원 많이 필요!");
            demoIdea2.setCategory("기술");
            demoIdea2.setLikes(15L);
            demoIdea2.setDislikes(3L);
            ideaRepository.save(demoIdea2);

            Idea demoIdea3 = new Idea();
            demoIdea3.setTitle("AI를 활용하여 뭉친 쓰레기 카메라 인식 분리수거 손");
            demoIdea3.setContent("AI로 카메라 붙여서 인식하고 학습 시켜서 손 쥐어주고 쓰레기 종량제 싹 분리해서 재활용 시키면 냄새로 고통 받을 사람 더 없고 매립도 덜고 좋을 듯. 그거 레일로 도로롱 하면서 소독하고 재활용 공장으로 이어지게. 로봇 활용하기 좋을 듯?");
            demoIdea3.setCategory("기술");
            demoIdea3.setLikes(15L);
            demoIdea3.setDislikes(3L);
            ideaRepository.save(demoIdea3);

            ideaList = (List<Idea>) ideaRepository.findAll();
        }
        
        model.addAttribute("ideaList", ideaList);
        return "idea";
    }
    
    // 아이디어 좋아요
    @PostMapping("/idea/like/{id}")
    public String likeIdea(@PathVariable Long id) {
        Idea idea = ideaRepository.findById(id).orElse(null);
        if (idea != null) {
            idea.setLikes(idea.getLikes() + 1);
            ideaRepository.save(idea);
        }
        return "redirect:/idea";
    }
    
    // 아이디어 싫어요
    @PostMapping("/idea/dislike/{id}")
    public String dislikeIdea(@PathVariable Long id) {
        Idea idea = ideaRepository.findById(id).orElse(null);
        if (idea != null) {
            idea.setDislikes(idea.getDislikes() + 1);
            ideaRepository.save(idea);
        }
        return "redirect:/idea";
    }

}
