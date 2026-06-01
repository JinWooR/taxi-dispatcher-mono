package com.taxidispatcher.services.account.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxidispatcher.shared.common.config.BaseSecurityConfig;
import com.taxidispatcher.shared.common.jwt.InternalApiKeyFilter;
import com.taxidispatcher.shared.common.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends BaseSecurityConfig {

    public SecurityConfig(ObjectMapper objectMapper,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          InternalApiKeyFilter internalApiKeyFilter) {
        super(objectMapper, jwtAuthenticationFilter, internalApiKeyFilter);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected String[] getAdditionalPaths() {
        return new String[]{
            "/auth/register",
            "/auth/login",
            "/auth/login/customer",
            "/auth/login/driver",
            "/auth/refresh",
            "/auth/logout",
            "/auth/upgrade/customer",
            "/auth/upgrade/driver"
        };
    }
}
