package com.treatsboot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * This class configures how the static content (js,etc) is handled from a server perspective.
 * Defines how certain files are cached on the browser using cache headers, etc
 */
@Configuration
public class StaticContentConfiguration extends WebMvcConfigurerAdapter
{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // TODO: cache control turned off for now, will reintroduce once driver contract is fully understood
        //        registry.addResourceHandler("**/driver.js")
        //                .addResourceLocations("classpath:/public/")
        //                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
        //                .resourceChain(false);
    }

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
