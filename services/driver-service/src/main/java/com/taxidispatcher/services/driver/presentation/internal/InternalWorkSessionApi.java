package com.taxidispatcher.services.driver.presentation.internal;

import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalWorkSession;
import com.taxidispatcher.shared.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 근무 세션 내부 API (서비스 간 통신 전용)
 * Authorization: ApiKey {DRIVER_INTERNAL_API_KEY} 필요
 */
@Tag(name = "Internal Work Session API", description = "서비스 간 통신용 근무 세션 API")
@SecurityRequirement(name = BaseOpenApiConfig.API_KEY_SCHEME)
public interface InternalWorkSessionApi {

    @Operation(summary = "근무 세션 단건 조회 (유효성 검증용)",
            description = "workSessionId로 근무 세션을 조회합니다. tracking-service가 segment 적재 전 " +
                    "소속 driverId 및 status 확인 목적으로 사용")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 API Key"),
            @ApiResponse(responseCode = "404", description = "근무 세션 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<DriverInternalWorkSession>> findById(@PathVariable String workSessionId);
}
