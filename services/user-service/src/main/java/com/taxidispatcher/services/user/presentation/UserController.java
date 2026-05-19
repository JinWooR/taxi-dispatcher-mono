package com.taxidispatcher.services.user.presentation;

import com.taxidispatcher.services.user.application.dto.request.RegisterUserRequest;
import com.taxidispatcher.services.user.application.dto.request.UpdateUserRequest;
import com.taxidispatcher.services.user.application.dto.response.UserProfileResponse;
import com.taxidispatcher.services.user.application.service.UserService;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 프로필 REST API
 */
@RestController
@RequestMapping("/users")
@Validated
public class UserController implements UserApi {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<UserProfileResponse>> registerProfile(@Valid @RequestBody RegisterUserRequest request) {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserProfileResponse response = userService.registerProfile(
                accountId,
                request.getName(),
                request.getPhone()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(response));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserProfileResponse>> getMyProfile() {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserProfileResponse response = userService.getMyProfile(accountId);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @PutMapping("/me")
    public ResponseEntity<CommonResponse<UserProfileResponse>> updateMyProfile(@Valid @RequestBody UpdateUserRequest request) {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserProfileResponse response = userService.updateMyProfile(
                accountId,
                request.getName(),
                request.getPhone()
        );

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile() {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();

        userService.deleteMyProfile(accountId);

        return ResponseEntity.noContent().build();
    }
}
