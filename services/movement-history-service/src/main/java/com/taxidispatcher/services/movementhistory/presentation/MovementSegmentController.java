package com.taxidispatcher.services.movementhistory.presentation;

import com.taxidispatcher.services.movementhistory.application.dto.request.StartWorkSessionSegmentRequest;
import com.taxidispatcher.services.movementhistory.application.dto.request.UpdateSegmentPolylineRequest;
import com.taxidispatcher.services.movementhistory.application.dto.response.DispatchMovementsResponse;
import com.taxidispatcher.services.movementhistory.application.dto.response.DriverPeriodMovementsResponse;
import com.taxidispatcher.services.movementhistory.application.dto.response.MovementSegmentResponse;
import com.taxidispatcher.services.movementhistory.application.dto.response.WorkSessionMovementsResponse;
import com.taxidispatcher.services.movementhistory.application.service.MovementSegmentService;
import com.taxidispatcher.shared.common.jwt.AuthUser;
import com.taxidispatcher.shared.common.request.DateRangeRequest;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movements")
@Validated
@RequiredArgsConstructor
public class MovementSegmentController implements MovementSegmentApi {

    private final MovementSegmentService service;

    @Override
    @PostMapping("/work-sessions/{workSessionId}/segments")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<MovementSegmentResponse>> startSegment(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable String workSessionId,
            @Valid @RequestBody StartWorkSessionSegmentRequest request) {
        MovementSegmentResponse response = service.start(workSessionId, authUser.getActor(), request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @PutMapping("/work-sessions/{workSessionId}/segments/{segmentId}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<MovementSegmentResponse>> updateSegmentPolyline(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable String workSessionId,
            @PathVariable Long segmentId,
            @Valid @RequestBody UpdateSegmentPolylineRequest request) {
        MovementSegmentResponse response = service.updatePolyline(segmentId, authUser.getActor(), request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @PostMapping("/work-sessions/{workSessionId}/segments/{segmentId}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<MovementSegmentResponse>> completeSegment(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable String workSessionId,
            @PathVariable Long segmentId) {
        MovementSegmentResponse response = service.complete(segmentId, authUser.getActor());
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @GetMapping("/work-sessions/{workSessionId}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<WorkSessionMovementsResponse>> getWorkSessionMovements(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable String workSessionId) {
        WorkSessionMovementsResponse response = service.findByWorkSessionId(workSessionId, authUser.getActor());
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @GetMapping("/dispatches/{dispatchId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'CUSTOMER')")
    public ResponseEntity<CommonResponse<DispatchMovementsResponse>> getDispatchMovements(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable String dispatchId) {
        DispatchMovementsResponse response = service.findByDispatchId(dispatchId, authUser);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @GetMapping("/drivers/me")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<DriverPeriodMovementsResponse>> getMyMovementsByPeriod(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid DateRangeRequest dateRange) {
        DriverPeriodMovementsResponse response = service.findMyPeriodMovements(
            authUser.getActor(),
            dateRange.toStartLocalDateTime(),
            dateRange.toEndLocalDateTime()
        );
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @GetMapping("/drivers/me/active-segment")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<MovementSegmentResponse>> getMyActiveSegment(
            @AuthenticationPrincipal AuthUser authUser) {
        MovementSegmentResponse response = service.findMyActiveSegment(authUser.getActor());
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
