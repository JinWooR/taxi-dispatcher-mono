package com.taxidispatcher.services.account.infrastructure.config;

import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountOpenApiConfig extends BaseOpenApiConfig {

    @Override
    protected Info apiInfo() {
        return new Info()
                .title("Account Service API")
                .description("계정 및 인증 관리 API")
                .version("1.0.0");
    }

    @Override
    protected Components buildComponents() {
        return super.buildComponents()
                .addSecuritySchemes(REFRESH_TOKEN_SCHEME, refreshTokenScheme());
    }
}
