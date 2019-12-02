package com.treatsboot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * This class configures how the static content (js,etc) is handled from a server perspective.
 * Defines how certain files are cached on the browser using cache headers, etc
 */
@Configuration
public class StaticContentConfiguration extends WebMvcConfigurerAdapter
{
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("GET").allowedOrigins("*")
                    .allowedHeaders("*");
            }
        };
    }
}
