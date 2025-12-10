package com.jvision.admin202318021;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                                 Object handler, ModelAndView modelAndView) throws Exception {
                if (modelAndView != null) {
                    // 인증 정보 처리
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null && authentication.isAuthenticated() 
                        && authentication.getPrincipal() instanceof OAuth2User) {
                        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                        Map<String, Object> principal = new HashMap<>();
                        principal.put("name", oauth2User.getAttribute("login"));
                        principal.put("authenticated", true);
                        modelAndView.addObject("principal", principal);
                    } else {
                        Map<String, Object> principal = new HashMap<>();
                        principal.put("authenticated", false);
                        modelAndView.addObject("principal", principal);
                    }
                    
                    // CSRF 토큰 처리
                    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                    if (csrfToken != null) {
                        Map<String, Object> csrf = new HashMap<>();
                        csrf.put("token", csrfToken.getToken());
                        modelAndView.addObject("_csrf", csrf);
                    }
                }
            }
        });
    }
}

