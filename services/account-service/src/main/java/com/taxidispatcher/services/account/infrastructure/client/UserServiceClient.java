package com.taxidispatcher.services.account.infrastructure.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxidispatcher.shared.common.dto.user.internal.UserInternalProfile;
import com.taxidispatcher.shared.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * user-service 내부 API 호출 클라이언트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final ObjectMapper objectMapper;

    @Value("${user-service.url}")
    private String userServiceUrl;

    @Value("${user-service.api-key}")
    private String userServiceApiKey;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(userServiceUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "ApiKey " + userServiceApiKey)
                .build();
    }

    /**
     * accountId로 사용자 프로필 조회
     * 프로필이 없으면 Optional.empty() 반환
     */
    public Optional<UserInternalProfile> findByAccountId(String accountId) {
        try {
            String body = restClient().get()
                    .uri("/internal/users/by-account/{accountId}", accountId)
                    .retrieve()
                    .body(String.class);

            CommonResponse<UserInternalProfile> response = objectMapper.readValue(
                    body,
                    new TypeReference<CommonResponse<UserInternalProfile>>() {}
            );
            return Optional.ofNullable(response.getData());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            log.error("user-service 호출 실패: status={}, accountId={}", e.getStatusCode(), accountId);
            throw new RuntimeException("user-service 호출 실패", e);

        } catch (Exception e) {
            log.error("user-service 응답 파싱 실패: accountId={}", accountId, e);
            throw new RuntimeException("user-service 응답 처리 실패", e);
        }
    }
}
