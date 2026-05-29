package com.taxidispatcher.services.driver.presentation.internal;

import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalProfile;
import com.taxidispatcher.shared.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 기사 내부 API (서비스 간 통신 전용)
 * Authorization: ApiKey {DRIVER_INTERNAL_API_KEY} 필요
 */
@Tag(name = "Internal Driver API", description = "서비스 간 통신용 내부 API")
@SecurityRequirement(name = BaseOpenApiConfig.API_KEY_SCHEME)
public interface InternalDriverApi {

    @Operation(summary = "accountId로 기사 프로필 조회",
            description = "계정 ID로 기사 프로필 정보를 조회합니다. 서비스 간 통신용")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 API Key"),
            @ApiResponse(responseCode = "404", description = "프로필 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<DriverInternalProfile>> findByAccountId(@PathVariable String accountId);

    @Operation(summary = "주변 ONLINE 기사 조회",
            description = "특정 위경도 기준 반경 내 ONLINE 상태 기사를 거리 오름차순으로 조회합니다. " +
                    "excludeDriverIds 파라미터로 이미 알림을 받은 기사 등 제외 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 API Key"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<List<DriverInternalProfile>>> findNearbyDrivers(
            @Parameter(description = "중심 위도", example = "37.5665") @RequestParam double latitude,
            @Parameter(description = "중심 경도", example = "126.9780") @RequestParam double longitude,
            @Parameter(description = "반경 (km)", example = "3.0") @RequestParam double radiusKm,
            @Parameter(description = "제외할 기사 ID 목록 (선택)", example = "id1,id2")
            @RequestParam(required = false) List<String> excludeDriverIds);
}
