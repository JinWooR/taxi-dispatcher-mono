package com.taxidispatcher.services.driver.presentation;

import com.taxidispatcher.services.driver.application.dto.response.WorkSessionResponse;
import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import com.taxidispatcher.shared.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Work Session API", description = "기사 근무 세션 관련 API")
@SecurityRequirement(name = BaseOpenApiConfig.ACCESS_TOKEN_SCHEME)
public interface WorkSessionApi {

    @Operation(summary = "현재 진행 중인 근무 세션 조회",
            description = "본인의 IN_PROGRESS 상태 근무 세션을 조회합니다. 없으면 404")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "진행 중인 근무 세션 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<WorkSessionResponse>> getCurrent();

    @Operation(summary = "본인 근무 세션 이력 조회",
            description = "본인의 근무 세션 목록을 페이징으로 조회합니다. startedAt DESC 정렬")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<Page<WorkSessionResponse>>> getHistory(
            @Parameter(description = "페이징 파라미터 (page, size)") Pageable pageable);

    @Operation(summary = "본인 근무 세션 단건 조회",
            description = "workSessionId로 본인의 근무 세션을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "타인의 근무 세션 접근 시도"),
            @ApiResponse(responseCode = "404", description = "근무 세션 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<WorkSessionResponse>> getById(
            @Parameter(description = "근무 세션 ID (UUID)") @PathVariable String workSessionId);
}
