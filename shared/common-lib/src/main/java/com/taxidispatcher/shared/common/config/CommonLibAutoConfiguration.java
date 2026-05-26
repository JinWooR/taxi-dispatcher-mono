package com.taxidispatcher.shared.common.config;

import com.taxidispatcher.shared.common.jwt.InternalApiKeyFilter;
import com.taxidispatcher.shared.common.jwt.JwtAuthenticationFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@AutoConfiguration
@ComponentScan(basePackages = "com.taxidispatcher.shared.common")
@EnableMethodSecurity
public class CommonLibAutoConfiguration {

    /**
     * Spring Boot의 자동 필터 등록 비활성화
     * SecurityFilterChain에서만 동작하도록 제한
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<InternalApiKeyFilter> internalApiKeyFilterRegistration(InternalApiKeyFilter filter) {
        FilterRegistrationBean<InternalApiKeyFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
