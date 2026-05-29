package com.taxidispatcher.services.driver.presentation.internal;

import com.taxidispatcher.services.driver.application.service.DriverService;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalProfile;
import com.taxidispatcher.shared.common.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Override
    @GetMapping("/nearby")
    public ResponseEntity<CommonResponse<List<DriverInternalProfile>>> findNearbyDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radiusKm,
            @RequestParam(required = false) List<String> excludeDriverIds) {
        List<DriverInternalProfile> drivers =
                driverService.findNearbyDrivers(latitude, longitude, radiusKm, excludeDriverIds);
        return ResponseEntity.ok(CommonResponse.success(drivers));
    }
}
