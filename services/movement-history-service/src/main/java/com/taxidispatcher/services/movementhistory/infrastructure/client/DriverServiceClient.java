package com.taxidispatcher.services.movementhistory.infrastructure.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalWorkSession;
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
     * workSessionId 로 근무 세션 조회 (segment 적재 전 유효성 검증용)
     * 근무 세션이 없으면 Optional.empty() 반환
     */
    public Optional<DriverInternalWorkSession> findWorkSession(String workSessionId) {
        try {
            String body = restClient().get()
                    .uri("/internal/work-sessions/{workSessionId}", workSessionId)
                    .retrieve()
                    .body(String.class);

            CommonResponse<DriverInternalWorkSession> response = objectMapper.readValue(
                    body,
                    new TypeReference<CommonResponse<DriverInternalWorkSession>>() {}
            );
            return Optional.ofNullable(response.getData());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            log.error("driver-service 호출 실패: status={}, workSessionId={}", e.getStatusCode(), workSessionId);
            throw new DomainException(
                    "DRIVER_SERVICE_UNAVAILABLE",
                    "기사 서비스에 일시적으로 연결할 수 없습니다. 잠시 후 다시 시도해주세요.",
                    HttpStatus.SERVICE_UNAVAILABLE);

        } catch (Exception e) {
            log.error("driver-service 호출 중 오류 발생: workSessionId={}", workSessionId, e);
            throw new DomainException(
                    "DRIVER_SERVICE_UNAVAILABLE",
                    "기사 서비스에 일시적으로 연결할 수 없습니다. 잠시 후 다시 시도해주세요.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
