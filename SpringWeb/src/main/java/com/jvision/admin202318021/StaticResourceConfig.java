package com.jvision.admin202318021;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/outputs/**")
                .addResourceLocations("file:outputs/");
    // serve runtime-generated analysis images from the project static folder so newly created
    // files are available without rebuilding the classpath
    registry.addResourceHandler("/analysis_outputs/**")
        .addResourceLocations("file:src/main/resources/static/analysis_outputs/");
    }
}

