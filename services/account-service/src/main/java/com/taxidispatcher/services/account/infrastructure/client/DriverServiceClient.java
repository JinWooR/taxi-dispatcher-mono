package com.taxidispatcher.services.account.infrastructure.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalProfile;
import com.taxidispatcher.shared.common.exception.DomainException;
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
 * driver-service 내부 API 호출 클라이언트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DriverServiceClient {

    private final ObjectMapper objectMapper;

    @Value("${driver-service.url}")
    private String driverServiceUrl;

    @Value("${driver-service.api-key}")
    private String driverServiceApiKey;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(driverServiceUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "ApiKey " + driverServiceApiKey)
                .build();
    }

    /**
     * accountId로 기사 프로필 조회
     * 프로필이 없으면 Optional.empty() 반환
     */
    public Optional<DriverInternalProfile> findByAccountId(String accountId) {
        try {
            String body = restClient().get()
                    .uri("/internal/drivers/by-account/{accountId}", accountId)
                    .retrieve()
                    .body(String.class);

            CommonResponse<DriverInternalProfile> response = objectMapper.readValue(
                    body,
                    new TypeReference<CommonResponse<DriverInternalProfile>>() {}
            );
            return Optional.ofNullable(response.getData());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            log.error("driver-service 호출 실패: status={}, accountId={}", e.getStatusCode(), accountId);
            throw new DomainException(
                    "DRIVER_SERVICE_UNAVAILABLE",
                    "기사 서비스에 일시적으로 연결할 수 없습니다. 잠시 후 다시 시도해주세요.",
                    HttpStatus.SERVICE_UNAVAILABLE);

        } catch (Exception e) {
            log.error("driver-service 호출 중 오류 발생: accountId={}", accountId, e);
            throw new DomainException(
                    "DRIVER_SERVICE_UNAVAILABLE",
                    "기사 서비스에 일시적으로 연결할 수 없습니다. 잠시 후 다시 시도해주세요.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
