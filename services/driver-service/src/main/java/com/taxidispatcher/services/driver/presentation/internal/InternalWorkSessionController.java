package com.taxidispatcher.services.driver.presentation.internal;

import com.taxidispatcher.services.driver.application.service.WorkSessionService;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalWorkSession;
import com.taxidispatcher.shared.common.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/work-sessions")
public class InternalWorkSessionController implements InternalWorkSessionApi {

    private final WorkSessionService workSessionService;

    public InternalWorkSessionController(WorkSessionService workSessionService) {
        this.workSessionService = workSessionService;
    }

    @Override
    @GetMapping("/{workSessionId}")
    public ResponseEntity<CommonResponse<DriverInternalWorkSession>> findById(@PathVariable String workSessionId) {
        DriverInternalWorkSession profile = workSessionService.findInternalProfileById(workSessionId);
        return ResponseEntity.ok(CommonResponse.success(profile));
    }
}
