package com.taxidispatcher.services.driver.infrastructure.config;

import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DriverOpenApiConfig extends BaseOpenApiConfig {

    @Override
    protected Info apiInfo() {
        return new Info()
                .title("Driver Service API")
                .description("기사 프로필 관리 API")
                .version("1.0.0");
    }
}
