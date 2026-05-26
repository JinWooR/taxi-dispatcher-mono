package com.taxidispatcher.shared.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;

public abstract class BaseOpenApiConfig {

    public static final String ACCESS_TOKEN_SCHEME = "accessToken";
    public static final String REFRESH_TOKEN_SCHEME = "refreshToken";
    public static final String API_KEY_SCHEME = "apiKey";

    /**
     * 전역 SecurityRequirement는 설정하지 않음
     * 각 API/Controller에서 @SecurityRequirement 어노테이션으로 명시적 지정
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(buildComponents());
    }

    protected abstract Info apiInfo();

    protected Components buildComponents() {
        return new Components()
                .addSecuritySchemes(ACCESS_TOKEN_SCHEME, accessTokenScheme())
                .addSecuritySchemes(API_KEY_SCHEME, apiKeyScheme());
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

    /**
     * 내부 API 호출용 ApiKey SecurityScheme
     * Authorization 헤더에 'ApiKey {각 서비스의 API Key}' 형식으로 입력
     * 각 서비스가 호출하려는 대상 서비스의 API Key를 사용해야 함
     * 예) account-service → user-service 호출 시 USER_INTERNAL_API_KEY 값 사용
     */
    protected SecurityScheme apiKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("내부 API 호출용. 형식: 'ApiKey {호출 대상 서비스의 API Key}'");
    }
}
