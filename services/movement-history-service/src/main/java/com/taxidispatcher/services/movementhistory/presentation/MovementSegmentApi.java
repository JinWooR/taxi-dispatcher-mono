package com.taxidispatcher.services.movementhistory.presentation;

import com.taxidispatcher.services.movementhistory.application.dto.request.StartWorkSessionSegmentRequest;
import com.taxidispatcher.services.movementhistory.application.dto.request.UpdateSegmentPolylineRequest;
import com.taxidispatcher.services.movementhistory.application.dto.response.DispatchMovementsResponse;
import com.taxidispatcher.services.movementhistory.application.dto.response.DriverPeriodMovementsResponse;
import com.taxidispatcher.services.movementhistory.application.dto.response.MovementSegmentResponse;
import com.taxidispatcher.services.movementhistory.application.dto.response.WorkSessionMovementsResponse;
import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import com.taxidispatcher.shared.common.jwt.AuthUser;
import com.taxidispatcher.shared.common.request.DateRangeRequest;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "Movement Segment API", description = "기사 근무 세션 이동 segment 관리 API")
@SecurityRequirement(name = BaseOpenApiConfig.ACCESS_TOKEN_SCHEME)
public interface MovementSegmentApi {

    @Operation(summary = "근무 세션 segment 시작",
            description = "새 segment 를 생성합니다. 배차 운행 중이면 dispatchId 도 함께 전달합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "기사 권한 필요"),
            @ApiResponse(responseCode = "409", description = "동일 segmentNo 중복")
    })
    ResponseEntity<CommonResponse<MovementSegmentResponse>> startSegment(
            AuthUser authUser,
            @Parameter(description = "Work Session ID") String workSessionId,
            @Valid StartWorkSessionSegmentRequest request);

    @Operation(summary = "진행 중 segment polyline 갱신",
            description = "IN_PROGRESS 상태 segment 의 polyline 을 덮어씁니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "갱신 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "본인 segment 아님"),
            @ApiResponse(responseCode = "404", description = "Segment 없음"),
            @ApiResponse(responseCode = "409", description = "이미 완료된 segment")
    })
    ResponseEntity<CommonResponse<MovementSegmentResponse>> updateSegmentPolyline(
            AuthUser authUser,
            @Parameter(description = "Work Session ID") String workSessionId,
            @Parameter(description = "Segment ID") Long segmentId,
            @Valid UpdateSegmentPolylineRequest request);

    @Operation(summary = "Segment 완료 (COMPLETED 전이)",
            description = "진행 중 segment 를 완료 상태로 전환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "완료 성공"),
            @ApiResponse(responseCode = "403", description = "본인 segment 아님"),
            @ApiResponse(responseCode = "404", description = "Segment 없음"),
            @ApiResponse(responseCode = "409", description = "이미 완료된 segment")
    })
    ResponseEntity<CommonResponse<MovementSegmentResponse>> completeSegment(
            AuthUser authUser,
            @Parameter(description = "Work Session ID") String workSessionId,
            @Parameter(description = "Segment ID") Long segmentId);

    @Operation(summary = "근무 세션 이동 이력 조회",
            description = "Work Session 단위의 모든 segment 를 segmentNo 오름차순으로 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "본인 근무 세션 아님")
    })
    ResponseEntity<CommonResponse<WorkSessionMovementsResponse>> getWorkSessionMovements(
            AuthUser authUser,
            @Parameter(description = "Work Session ID") String workSessionId);

    @Operation(summary = "배차 단위 이동 이력 조회",
            description = "배차에 속한 segment (segment.dispatch_id 매칭) 를 반환합니다. " +
                    "driver/customer 모두 접근 가능하며, driver 토큰만 본인 driverId 일치 검증을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "기사 토큰의 driverId 와 불일치")
    })
    ResponseEntity<CommonResponse<DispatchMovementsResponse>> getDispatchMovements(
            AuthUser authUser,
            @Parameter(description = "Dispatch ID") String dispatchId);

    @Operation(summary = "내 이동 이력 기간별 조회",
            description = "기사 본인의 segment 를 startedAt 기준 기간 범위로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "startDate / endDate 누락"),
            @ApiResponse(responseCode = "403", description = "기사 권한 필요")
    })
    ResponseEntity<CommonResponse<DriverPeriodMovementsResponse>> getMyMovementsByPeriod(
            AuthUser authUser,
            @ParameterObject @Valid DateRangeRequest dateRange);

    @Operation(summary = "내 현재 진행 중 segment 조회",
            description = "클라이언트 segmentId 유실 복구용. IN_PROGRESS 상태 중 가장 최근 시작된 segment 1개를 반환합니다. 없으면 data 는 null.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공 (활성 segment 없으면 data:null)"),
            @ApiResponse(responseCode = "403", description = "기사 권한 필요")
    })
    ResponseEntity<CommonResponse<MovementSegmentResponse>> getMyActiveSegment(AuthUser authUser);
}
