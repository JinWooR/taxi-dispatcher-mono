package com.taxidispatcher.services.driver.presentation.internal;

import com.taxidispatcher.services.driver.application.service.DriverService;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalProfile;
import com.taxidispatcher.shared.common.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/drivers")
public class InternalDriverController implements InternalDriverApi {

    private final DriverService driverService;

    public InternalDriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @Override
    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<CommonResponse<DriverInternalProfile>> findByAccountId(@PathVariable String accountId) {
        DriverInternalProfile profile = driverService.findProfileByAccountId(accountId);
        return ResponseEntity.ok(CommonResponse.success(profile));
    }
}
