package com.taxidispatcher.shared.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;

import java.util.List;

public abstract class BaseOpenApiConfig {

    public static final String ACCESS_TOKEN_SCHEME = "accessToken";
    public static final String REFRESH_TOKEN_SCHEME = "refreshToken";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(buildComponents())
                .security(buildSecurityRequirements());
    }

    protected abstract Info apiInfo();

    protected Components buildComponents() {
        return new Components()
                .addSecuritySchemes(ACCESS_TOKEN_SCHEME, accessTokenScheme());
    }

    protected List<SecurityRequirement> buildSecurityRequirements() {
        return List.of(new SecurityRequirement().addList(ACCESS_TOKEN_SCHEME));
    }

    protected SecurityScheme accessTokenScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Access Token");
    }

    protected SecurityScheme refreshTokenScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Refresh Token");
    }
}
