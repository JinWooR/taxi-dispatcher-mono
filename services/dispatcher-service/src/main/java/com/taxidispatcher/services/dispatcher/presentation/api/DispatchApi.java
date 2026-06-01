package com.taxidispatcher.services.dispatcher.presentation.api;

import com.taxidispatcher.services.dispatcher.application.dto.request.CreateDispatchRequest;
import com.taxidispatcher.services.dispatcher.application.dto.request.UpdateDispatchStatusRequest;
import com.taxidispatcher.services.dispatcher.application.dto.response.DispatchResponse;
import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import com.taxidispatcher.shared.common.jwt.AuthUser;
import com.taxidispatcher.shared.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 배차 API (Swagger 문서)
 * Spring MVC 매핑 어노테이션은 DispatchController에서 직접 선언
 */
@Tag(name = "Dispatch", description = "배차 관리")
@SecurityRequirement(name = BaseOpenApiConfig.ACCESS_TOKEN_SCHEME)
public interface DispatchApi {

    // ===== Customer Endpoints =====

    @Operation(summary = "배차 요청 생성", description = "고객가 새로운 배차 요청 생성")
    @ApiResponse(responseCode = "201", description = "배차 요청 생성 성공")
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<DispatchResponse>> createDispatch(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateDispatchRequest request);

    @Operation(summary = "내 배차 목록 조회", description = "로그인한 고객의 배차 목록 조회 (페이징)")
    @ApiResponse(responseCode = "200", description = "배차 목록 조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<Page<DispatchResponse>>> getMyDispatches(
            @AuthenticationPrincipal AuthUser authUser,
            Pageable pageable);

    // ===== Driver Endpoints =====

    @Operation(summary = "Pending 배차 목록 조회", description = "기사에게 온 pending 배차 목록 조회 (페이징)")
    @ApiResponse(responseCode = "200", description = "Pending 배차 목록 조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<Page<DispatchResponse>>> getPendingDispatches(
            @AuthenticationPrincipal AuthUser authUser,
            Pageable pageable);

    @Operation(summary = "배차 승인", description = "기사가 배차 요청 승인")
    @ApiResponse(responseCode = "200", description = "배차 승인 성공")
    @ApiResponse(responseCode = "404", description = "배차를 찾을 수 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "409", description = "상태 전이 불가", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<DispatchResponse>> acceptDispatch(
            @Parameter(description = "배차 ID") @PathVariable String dispatchId,
            @AuthenticationPrincipal AuthUser authUser);

    @Operation(summary = "배차 거절", description = "기사가 배차 요청 거절")
    @ApiResponse(responseCode = "200", description = "배차 거절 처리 성공")
    @ApiResponse(responseCode = "404", description = "배차를 찾을 수 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<DispatchResponse>> rejectDispatch(
            @Parameter(description = "배차 ID") @PathVariable String dispatchId,
            @AuthenticationPrincipal AuthUser authUser);

    // ===== Shared Endpoints =====

    @Operation(summary = "배차 상세 조회", description = "배차 ID로 상세 정보 조회")
    @ApiResponse(responseCode = "200", description = "배차 조회 성공")
    @ApiResponse(responseCode = "404", description = "배차를 찾을 수 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<DispatchResponse>> getDispatch(
            @Parameter(description = "배차 ID") @PathVariable String dispatchId);

    @Operation(summary = "배차 상태 변경", description = "배차 상태 전이 (운행중, 도착, 완료 등)")
    @ApiResponse(responseCode = "200", description = "상태 변경 성공")
    @ApiResponse(responseCode = "404", description = "배차를 찾을 수 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "409", description = "상태 전이 불가", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<DispatchResponse>> updateStatus(
            @Parameter(description = "배차 ID") @PathVariable String dispatchId,
            @Valid @RequestBody UpdateDispatchStatusRequest request);
}
