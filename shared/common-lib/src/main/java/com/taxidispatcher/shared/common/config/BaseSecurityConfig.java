package com.taxidispatcher.shared.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxidispatcher.shared.common.jwt.InternalApiKeyFilter;
import com.taxidispatcher.shared.common.jwt.JwtAuthenticationFilter;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public abstract class BaseSecurityConfig {

    protected final ObjectMapper objectMapper;
    protected final JwtAuthenticationFilter jwtAuthenticationFilter;
    protected final InternalApiKeyFilter internalApiKeyFilter;

    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:8090}")
    private String corsAllowedOrigins;

    // 모든 서비스가 공통으로 허용하는 경로
    private static final String[] COMMON_PUBLIC_PATHS = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/v3/api-docs.yaml",
        "/actuator/health",
        "/actuator/health/**"
    };

    /**
     * 내부 API용 SecurityFilterChain
     * /internal/** 경로에 ApiKey 인증 적용
     */
    @Bean
    @Order(1)
    public SecurityFilterChain internalSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .securityMatcher("/internal/**")
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(internalApiKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 외부 API용 SecurityFilterChain
     * JWT 인증 적용
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] allPublicPaths = combinePublicPaths();

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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

    /**
     * CORS 설정
     * 환경변수 CORS_ALLOWED_ORIGINS로 허용 origin 제어 (콤마로 구분)
     * 현재 /v3/api-docs/** 경로에만 적용 (통합 Swagger UI 지원)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(corsAllowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 전체 경로
        return source;
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
