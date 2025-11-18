package com.jvision.admin202318021.config;

import com.jvision.admin202318021.entity.PolicyPost;
import com.jvision.admin202318021.repository.PolicyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final PolicyPostRepository policyPostRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // 이미 데이터가 있으면 초기화하지 않음
        if (policyPostRepository.count() > 0) {
            return;
        }
        
        // 데모 데이터 생성
        PolicyPost post1 = new PolicyPost();
        post1.setTitle("재활용 플라스틱 신제품 제조 공장 지원 정책");
        post1.setContent("재활용 플라스틱을 원료로 사용하여 신제품을 제조하는 공장에 대한 지원 정책을 제안합니다.\n\n" +
                "1. 설비 투자 지원: 재활용 플라스틱 가공 설비 구축 시 최대 50% 지원\n" +
                "2. 세제 혜택: 재활용 원료 사용 시 법인세 감면\n" +
                "3. 기술 지원: 재활용 플라스틱 가공 기술 컨설팅 제공\n" +
                "4. 인증 제도: 재활용 원료 사용 제품에 대한 친환경 인증 마크 부여\n\n" +
                "이를 통해 재활용 플라스틱의 수요를 증가시키고 순환 경제를 활성화할 수 있습니다.");
        post1.setCategory("재활용제조공장지원");
        post1.setAuthor("환경정책연구소");
        post1.setAuthorEmail("demo@example.com");
        post1.setViewCount(125);
        post1.setCreatedAt(LocalDateTime.now().minusDays(5));
        post1.setUpdatedAt(LocalDateTime.now().minusDays(5));
        
        PolicyPost post2 = new PolicyPost();
        post2.setTitle("재활용 플라스틱 수입 및 취급 공장 지원 방안");
        post2.setContent("재활용 플라스틱을 수집하고 수입하여 재가공하는 공장에 대한 지원 정책입니다.\n\n" +
                "1. 수집 인프라 지원: 재활용 플라스틱 수집 네트워크 구축 지원\n" +
                "2. 품질 관리 지원: 재활용 원료 품질 검사 시설 및 인력 지원\n" +
                "3. 물류 지원: 재활용 플라스틱 운송비 일부 지원\n" +
                "4. 수입 허가 간소화: 재활용 플라스틱 수입 절차 간소화 및 세금 감면\n\n" +
                "재활용 플라스틱의 안정적인 공급망 구축이 필요합니다.");
        post2.setCategory("재활용수입공장지원");
        post2.setAuthor("순환경제협회");
        post2.setAuthorEmail("demo@example.com");
        post2.setViewCount(98);
        post2.setCreatedAt(LocalDateTime.now().minusDays(3));
        post2.setUpdatedAt(LocalDateTime.now().minusDays(3));
        
        PolicyPost post3 = new PolicyPost();
        post3.setTitle("폐플라스틱 수거 및 재료화 공장 지원 정책");
        post3.setContent("폐플라스틱을 수거하여 재활용 원료로 변환하는 공장에 대한 지원 정책을 제안합니다.\n\n" +
                "1. 수거 인센티브: 폐플라스틱 수거량에 따른 보조금 지급\n" +
                "2. 재처리 설비 지원: 폐플라스틱 선별 및 재처리 설비 구축 지원\n" +
                "3. 환경 규제 완화: 재활용 공장에 대한 환경 인허가 절차 간소화\n" +
                "4. R&D 지원: 고품질 재활용 원료 생산 기술 개발 지원\n\n" +
                "폐플라스틱을 자원으로 순환시키는 사업 모델을 지원하여 순환 경제를 실현하겠습니다.");
        post3.setCategory("폐기물재활용공장지원");
        post3.setAuthor("환경부 정책팀");
        post3.setAuthorEmail("demo@example.com");
        post3.setViewCount(156);
        post3.setCreatedAt(LocalDateTime.now().minusDays(7));
        post3.setUpdatedAt(LocalDateTime.now().minusDays(7));
        
        PolicyPost post4 = new PolicyPost();
        post4.setTitle("재활용 플라스틱 순환 경제 생태계 구축 방안");
        post4.setContent("재활용 플라스틱이 순환될 수 있도록 전체 생태계를 구축하는 정책입니다.\n\n" +
                "1. 플랫폼 구축: 재활용 플라스틱 거래 플랫폼 구축 지원\n" +
                "2. 공급망 연결: 수거-재처리-제조 공장 간 네트워크 구축\n" +
                "3. 표준화: 재활용 플라스틱 품질 기준 및 인증 제도 마련\n" +
                "4. 금융 지원: 순환 경제 기업에 대한 저리 융자 제공\n\n" +
                "재활용 플라스틱이 지속 가능하게 순환될 수 있는 생태계를 만들어야 합니다.");
        post4.setCategory("순환경제생태계");
        post4.setAuthor("친환경산업연구원");
        post4.setAuthorEmail("demo@example.com");
        post4.setViewCount(87);
        post4.setCreatedAt(LocalDateTime.now().minusDays(2));
        post4.setUpdatedAt(LocalDateTime.now().minusDays(2));
        
        PolicyPost post5 = new PolicyPost();
        post5.setTitle("재활용 플라스틱 제품 제조 기업 세제 혜택 확대");
        post5.setContent("재활용 플라스틱을 사용하여 제품을 제조하는 기업에 대한 세제 혜택을 확대하는 정책입니다.\n\n" +
                "1. 법인세 감면: 재활용 원료 사용 비율에 따른 법인세 최대 30% 감면\n" +
                "2. 부가가치세 면제: 재활용 플라스틱 제품에 대한 부가가치세 면제\n" +
                "3. 연구개발비 세액공제: 재활용 기술 개발비 세액공제 확대\n" +
                "4. 수출 지원: 재활용 제품 수출 시 관세 혜택\n\n" +
                "기업이 재활용 플라스틱을 적극 활용하도록 경제적 인센티브를 제공합니다.");
        post5.setCategory("재활용제조공장지원");
        post5.setAuthor("경제부 정책과");
        post5.setAuthorEmail("demo@example.com");
        post5.setViewCount(142);
        post5.setCreatedAt(LocalDateTime.now().minusDays(4));
        post5.setUpdatedAt(LocalDateTime.now().minusDays(4));
        
        PolicyPost post6 = new PolicyPost();
        post6.setTitle("폐플라스틱 수거 기업 인센티브 제도");
        post6.setContent("폐플라스틱을 체계적으로 수거하는 기업에 대한 인센티브 제도를 도입합니다.\n\n" +
                "1. 수거량 기반 보조금: 수거한 폐플라스틱 톤당 보조금 지급\n" +
                "2. 인증 제도: 우수 수거 기업 인증 및 홍보 지원\n" +
                "3. 기술 지원: 효율적인 수거 시스템 구축 컨설팅 제공\n" +
                "4. 협력 네트워크: 재처리 공장과의 협력 네트워크 연결 지원\n\n" +
                "폐플라스틱 수거가 수익성 있는 사업이 되도록 지원합니다.");
        post6.setCategory("폐기물재활용공장지원");
        post6.setAuthor("자원순환협회");
        post6.setAuthorEmail("demo@example.com");
        post6.setViewCount(103);
        post6.setCreatedAt(LocalDateTime.now().minusDays(1));
        post6.setUpdatedAt(LocalDateTime.now().minusDays(1));
        
        policyPostRepository.save(post1);
        policyPostRepository.save(post2);
        policyPostRepository.save(post3);
        policyPostRepository.save(post4);
        policyPostRepository.save(post5);
        policyPostRepository.save(post6);
    }
}

