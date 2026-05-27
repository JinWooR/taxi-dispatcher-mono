package com.taxidispatcher.services.user.presentation.internal;

import com.taxidispatcher.services.user.application.service.UserService;
import com.taxidispatcher.shared.common.dto.user.internal.UserInternalProfile;
import com.taxidispatcher.shared.common.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController implements InternalUserApi {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<CommonResponse<UserInternalProfile>> findByAccountId(@PathVariable String accountId) {
        UserInternalProfile profile = userService.findProfileByAccountId(accountId);
        return ResponseEntity.ok(CommonResponse.success(profile));
    }
}
