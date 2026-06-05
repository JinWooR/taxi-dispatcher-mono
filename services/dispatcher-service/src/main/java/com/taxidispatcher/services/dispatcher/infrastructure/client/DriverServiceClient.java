package com.taxidispatcher.services.dispatcher.infrastructure.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalProfile;
import com.taxidispatcher.shared.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

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
     * 주변 기사 목록 조회 (ONLINE 상태인 기사만 반환)
     */
    public List<DriverInternalProfile> findNearbyDrivers(
            double latitude,
            double longitude,
            double radiusKm,
            List<String> excludeDriverIds
    ) {
        try {
            String uri = buildNearbyUri(latitude, longitude, radiusKm, excludeDriverIds);

            String body = restClient().get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            CommonResponse<List<DriverInternalProfile>> response = objectMapper.readValue(
                    body,
                    new TypeReference<CommonResponse<List<DriverInternalProfile>>>() {}
            );
            List<DriverInternalProfile> data = response.getData();
            return data != null ? data : Collections.emptyList();

        } catch (Exception e) {
            log.error("driver-service nearby 호출 실패: lat={}, lng={}, radius={}km",
                    latitude, longitude, radiusKm, e);
            throw new RuntimeException("driver-service 주변 기사 조회 실패", e);
        }
    }

    private String buildNearbyUri(double latitude, double longitude, double radiusKm,
                                  List<String> excludeDriverIds) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/internal/drivers/nearby")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("radiusKm", radiusKm);

        if (excludeDriverIds != null && !excludeDriverIds.isEmpty()) {
            builder.queryParam("excludeDriverIds", excludeDriverIds.toArray());
        }

        return builder.build().toUriString();
    }
}
