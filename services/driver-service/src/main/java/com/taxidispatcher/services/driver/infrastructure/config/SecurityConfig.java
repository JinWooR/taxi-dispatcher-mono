package com.taxidispatcher.services.driver.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxidispatcher.shared.common.config.BaseSecurityConfig;
import com.taxidispatcher.shared.common.jwt.InternalApiKeyFilter;
import com.taxidispatcher.shared.common.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends BaseSecurityConfig {

    public SecurityConfig(ObjectMapper objectMapper,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          InternalApiKeyFilter internalApiKeyFilter) {
        super(objectMapper, jwtAuthenticationFilter, internalApiKeyFilter);
    }
}
