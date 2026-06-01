package com.taxidispatcher.services.customer.infrastructure.config;

import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerOpenApiConfig extends BaseOpenApiConfig {

    @Override
    protected Info apiInfo() {
        return new Info()
                .title("Customer Service API")
                .description("사용자 프로필 관리 API")
                .version("1.0.0");
    }
}
