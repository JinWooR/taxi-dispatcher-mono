package com.taxidispatcher.shared.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxidispatcher.shared.common.jwt.JwtAuthenticationFilter;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class BaseSecurityConfig {

    protected final ObjectMapper objectMapper;
    protected final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 모든 서비스가 공통으로 허용하는 경로
    private static final String[] COMMON_PUBLIC_PATHS = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/v3/api-docs.yaml",
        "/actuator/health",
        "/actuator/health/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] allPublicPaths = combinePublicPaths();

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(allPublicPaths).permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint())
            );

        return http.build();
    }

    // 서비스별 추가로 필요한 공개 경로 (기본값: 없음)
    protected String[] getAdditionalPaths() {
        return new String[]{};
    }

    private String[] combinePublicPaths() {
        List<String> combined = new ArrayList<>();
        for (String path : COMMON_PUBLIC_PATHS) {
            combined.add(path);
        }
        for (String path : getAdditionalPaths()) {
            combined.add(path);
        }
        return combined.toArray(new String[0]);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            CommonResponse<Object> errorResponse = CommonResponse.error(
                "UNAUTHORIZED",
                "인증이 필요합니다",
                request.getRequestURI()
            );

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        };
    }

}
