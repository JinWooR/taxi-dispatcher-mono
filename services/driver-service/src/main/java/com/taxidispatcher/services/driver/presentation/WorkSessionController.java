package com.taxidispatcher.services.driver.presentation;

import com.taxidispatcher.services.driver.application.dto.response.WorkSessionResponse;
import com.taxidispatcher.services.driver.application.service.DriverService;
import com.taxidispatcher.services.driver.application.service.WorkSessionService;
import com.taxidispatcher.services.driver.domain.driver.Driver;
import com.taxidispatcher.services.driver.domain.worksession.WorkSession;
import com.taxidispatcher.shared.common.exception.DomainException;
import com.taxidispatcher.shared.common.jwt.AuthUser;
import com.taxidispatcher.shared.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/drivers/me")
@RequiredArgsConstructor
public class WorkSessionController implements WorkSessionApi {

    private final DriverService driverService;
    private final WorkSessionService workSessionService;

    @Override
    @GetMapping("/current-work-session")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<WorkSessionResponse>> getCurrent() {
        String driverId = resolveDriverId();
        WorkSession workSession = workSessionService.findCurrentByDriverId(driverId);
        return ResponseEntity.ok(CommonResponse.success(WorkSessionResponse.from(workSession), "조회 성공"));
    }

    @Override
    @GetMapping("/work-sessions")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<Page<WorkSessionResponse>>> getHistory(
            @PageableDefault(size = 20, sort = "startedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String driverId = resolveDriverId();
        Page<WorkSessionResponse> page = workSessionService.findHistoryByDriverId(driverId, pageable)
                .map(WorkSessionResponse::from);
        return ResponseEntity.ok(CommonResponse.success(page, "조회 성공"));
    }

    @Override
    @GetMapping("/work-sessions/{workSessionId}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<WorkSessionResponse>> getById(@PathVariable String workSessionId) {
        String driverId = resolveDriverId();
        WorkSession workSession = workSessionService.findById(workSessionId);
        if (!workSession.getDriverId().equals(driverId)) {
            throw new DomainException(
                    "WORK_SESSION_FORBIDDEN", "본인의 근무 세션이 아닙니다", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(CommonResponse.success(WorkSessionResponse.from(workSession), "조회 성공"));
    }

    private String resolveDriverId() {
        String accountId = getAccountIdFromContext();
        Driver driver = driverService.getDriverByAccountId(accountId);
        return driver.getDriverId().getValue();
    }

    private String getAccountIdFromContext() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUser) {
            AuthUser authUser = (AuthUser) authentication.getPrincipal();
            return authUser.getAccountId().toString();
        }
        throw new IllegalStateException("인증 정보를 찾을 수 없습니다");
    }
}
