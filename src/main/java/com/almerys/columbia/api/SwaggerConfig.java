package com.almerys.columbia.api;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;

import static org.springframework.security.config.http.MatcherType.ant;
import static springfox.documentation.builders.PathSelectors.ant;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {
    @Bean
    public Docket productApi() {

        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.almerys.columbia.api.controllers"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());

    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Columbia REST API",
                "Provide a REST API for Columbia.",
                "1.2.0",
                "",
                new Contact(null, null, null),
                "GNU GPL", "https://www.gnu.org/licenses/licenses.fr.html", Collections.emptyList());
    }
}

