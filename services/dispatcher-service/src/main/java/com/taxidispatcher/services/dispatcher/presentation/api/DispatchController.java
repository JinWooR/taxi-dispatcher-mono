package com.taxidispatcher.services.dispatcher.presentation.api;

import com.taxidispatcher.services.dispatcher.application.dto.request.CreateDispatchRequest;
import com.taxidispatcher.services.dispatcher.application.dto.request.UpdateDispatchStatusRequest;
import com.taxidispatcher.services.dispatcher.application.dto.response.DispatchResponse;
import com.taxidispatcher.services.dispatcher.application.service.DispatchService;
import com.taxidispatcher.shared.common.jwt.AuthUser;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatches")
@RequiredArgsConstructor
@Validated
public class DispatchController implements DispatchApi {

    private final DispatchService dispatchService;

    // ===== Customer Endpoints =====

    @PostMapping("/customers")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CommonResponse<DispatchResponse>> createDispatch(
        @AuthenticationPrincipal AuthUser authUser,
        @Valid @RequestBody CreateDispatchRequest request
    ) {
        DispatchResponse response = dispatchService.createDispatch(authUser.getActor(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(response));
    }

    @GetMapping("/customers")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CommonResponse<Page<DispatchResponse>>> getMyDispatches(
        @AuthenticationPrincipal AuthUser authUser,
        Pageable pageable
    ) {
        Page<DispatchResponse> response = dispatchService.getDispatchesByCustomer(authUser.getActor(), pageable);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    // ===== Driver Endpoints =====

    @GetMapping("/drivers/pending")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<Page<DispatchResponse>>> getPendingDispatches(
        @AuthenticationPrincipal AuthUser authUser,
        Pageable pageable
    ) {
        Page<DispatchResponse> response = dispatchService.getPendingDispatches(pageable);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @PostMapping("/drivers/{dispatchId}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<DispatchResponse>> acceptDispatch(
        @PathVariable String dispatchId,
        @AuthenticationPrincipal AuthUser authUser
    ) {
        DispatchResponse response = dispatchService.acceptDispatch(dispatchId, authUser.getActor());
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @PostMapping("/drivers/{dispatchId}/reject")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<DispatchResponse>> rejectDispatch(
        @PathVariable String dispatchId,
        @AuthenticationPrincipal AuthUser authUser
    ) {
        DispatchResponse response = dispatchService.rejectDispatch(dispatchId, authUser.getActor());
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    // ===== Shared Endpoints =====

    @GetMapping("/{dispatchId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER')")
    public ResponseEntity<CommonResponse<DispatchResponse>> getDispatch(
        @PathVariable String dispatchId
    ) {
        DispatchResponse response = dispatchService.getDispatch(dispatchId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @PutMapping("/{dispatchId}/status")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER')")
    public ResponseEntity<CommonResponse<DispatchResponse>> updateStatus(
        @PathVariable String dispatchId,
        @Valid @RequestBody UpdateDispatchStatusRequest request
    ) {
        DispatchResponse response = dispatchService.updateDispatchStatus(dispatchId, request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
