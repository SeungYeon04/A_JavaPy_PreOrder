package com.jvision.admin202318021.service;

import com.jvision.admin202318021.entity.User;
import com.jvision.admin202318021.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService {
    
    private final UserRepository userRepository;
    
    public User processOAuth2User(OAuth2User oauth2User, String provider) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = null;
        String name = null;
        String providerId = null;
        String picture = null;
        
        if ("google".equals(provider)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            providerId = (String) attributes.get("sub");
            picture = (String) attributes.get("picture");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response != null) {
                email = (String) response.get("email");
                name = (String) response.get("name");
                providerId = (String) response.get("id");
                picture = (String) response.get("profile_image");
            }
        }
        
        if (email == null) {
            return null;
        }
        
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElse(null);
        
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : "사용자");
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setPicture(picture);
            user = userRepository.save(user);
        } else {
            // 정보 업데이트
            user.setName(name != null ? name : user.getName());
            user.setPicture(picture != null ? picture : user.getPicture());
            user = userRepository.save(user);
        }
        
        return user;
    }
}

