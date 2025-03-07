package com.flab.ecommerce.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "E-commerce API",
                description = "이커머스 프로젝트 API 명세서",
                version = "v1"
        )
)
public class SwaggerConfig {
}
