package com.taxidispatcher.services.dispatcher.infrastructure.config;

import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DispatcherOpenApiConfig extends BaseOpenApiConfig {

    @Override
    protected Info apiInfo() {
        return new Info()
                .title("Dispatcher Service API")
                .description("택시 배차 관리 API")
                .version("1.0.0");
    }
}
