package com.taxidispatcher.services.driver.presentation;

import com.taxidispatcher.services.driver.application.dto.request.ChangeStatusRequest;
import com.taxidispatcher.services.driver.application.dto.request.RegisterDriverRequest;
import com.taxidispatcher.services.driver.application.dto.request.UpdateDriverRequest;
import com.taxidispatcher.services.driver.application.dto.response.DriverResponse;
import com.taxidispatcher.services.driver.application.service.DriverService;
import com.taxidispatcher.services.driver.domain.driver.Driver;
import com.taxidispatcher.shared.common.jwt.AuthUser;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/drivers")
@Validated
@RequiredArgsConstructor
public class DriverController implements DriverApi {

    private final DriverService driverService;

    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('NONE', 'DRIVER')")
    public ResponseEntity<CommonResponse<DriverResponse>> register(@Valid @RequestBody RegisterDriverRequest request) {
        String accountId = getAccountIdFromContext();
        Driver driver = driverService.registerDriver(accountId, request);
        return ResponseEntity.ok(CommonResponse.success(DriverResponse.from(driver), "기사 등록 완료"));
    }

    @Override
    @GetMapping("/me")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<DriverResponse>> getMe() {
        String accountId = getAccountIdFromContext();
        Driver driver = driverService.getDriverByAccountId(accountId);
        return ResponseEntity.ok(CommonResponse.success(DriverResponse.from(driver), "조회 성공"));
    }

    @Override
    @PutMapping("/me")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<DriverResponse>> updateProfile(@Valid @RequestBody UpdateDriverRequest request) {
        String accountId = getAccountIdFromContext();
        Driver driver = driverService.updateDriver(accountId, request);
        return ResponseEntity.ok(CommonResponse.success(DriverResponse.from(driver), "프로필 수정 완료"));
    }

    @Override
    @PatchMapping("/me/status")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<CommonResponse<Void>> changeStatus(@Valid @RequestBody ChangeStatusRequest request) {
        String accountId = getAccountIdFromContext();
        driverService.changeStatus(accountId, request.getStatus());
        return ResponseEntity.ok(CommonResponse.success(null, "상태 변경 완료"));
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CommonResponse<Object>> getAvailableDrivers() {
        var drivers = driverService.getAvailableDrivers()
                .stream()
                .map(DriverResponse::from)
                .toList();
        return ResponseEntity.ok(CommonResponse.success(drivers, "온라인 기사 조회 성공"));
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
