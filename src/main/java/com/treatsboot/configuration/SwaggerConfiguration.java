package com.treatsboot.configuration;

import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * Defines Swagger
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer
{

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket treatsApi()
    {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .build()
                .apiInfo(apiInfo())
                .pathMapping("/")
                .directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .ignoredParameterTypes(ApiIgnore.class)
                .alternateTypeRules(
                        newRule(
                                typeResolver.resolve(
                                        DeferredResult.class,
                                        typeResolver.resolve(
                                                ResponseEntity.class,
                                                WildcardType.class)
                                ),
                                typeResolver.resolve(WildcardType.class)))
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder()
                .title("Treat Dispenser!")
                .description("Spy on Harley :)")
                .version("1.0")
                .build();
    }
}
