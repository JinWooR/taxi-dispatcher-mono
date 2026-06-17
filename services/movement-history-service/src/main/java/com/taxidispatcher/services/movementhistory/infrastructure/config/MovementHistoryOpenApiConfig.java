package com.taxidispatcher.services.movementhistory.infrastructure.config;

import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MovementHistoryOpenApiConfig extends BaseOpenApiConfig {

    @Override
    protected Info apiInfo() {
        return new Info()
                .title("Movement History Service API")
                .description("기사 이동 이력 보관 및 조회 API")
                .version("1.0.0");
    }
}
