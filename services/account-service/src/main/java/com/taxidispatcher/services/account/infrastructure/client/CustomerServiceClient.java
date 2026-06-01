package com.taxidispatcher.services.account.infrastructure.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxidispatcher.shared.common.dto.customer.internal.CustomerInternalProfile;
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
 * customer-service 내부 API 호출 클라이언트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerServiceClient {

    private final ObjectMapper objectMapper;

    @Value("${customer-service.url}")
    private String customerServiceUrl;

    @Value("${customer-service.api-key}")
    private String customerServiceApiKey;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(customerServiceUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "ApiKey " + customerServiceApiKey)
                .build();
    }

    /**
     * accountId로 고객 프로필 조회
     * 프로필이 없으면 Optional.empty() 반환
     */
    public Optional<CustomerInternalProfile> findByAccountId(String accountId) {
        try {
            String body = restClient().get()
                    .uri("/internal/customers/by-account/{accountId}", accountId)
                    .retrieve()
                    .body(String.class);

            CommonResponse<CustomerInternalProfile> response = objectMapper.readValue(
                    body,
                    new TypeReference<CommonResponse<CustomerInternalProfile>>() {}
            );
            return Optional.ofNullable(response.getData());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            log.error("customer-service 호출 실패: status={}, accountId={}", e.getStatusCode(), accountId);
            throw new RuntimeException("customer-service 호출 실패", e);

        } catch (Exception e) {
            log.error("customer-service 응답 파싱 실패: accountId={}", accountId, e);
            throw new RuntimeException("customer-service 응답 처리 실패", e);
        }
    }
}
