package com.taxidispatcher.services.user.presentation;

import com.taxidispatcher.services.user.application.dto.request.RegisterUserRequest;
import com.taxidispatcher.services.user.application.dto.request.UpdateUserRequest;
import com.taxidispatcher.services.user.application.dto.response.UserProfileResponse;
import com.taxidispatcher.services.user.application.service.UserService;
import com.taxidispatcher.shared.common.jwt.AuthUser;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAnyRole('NONE', 'USER')")
    public ResponseEntity<CommonResponse<UserProfileResponse>> registerProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody RegisterUserRequest request) {

        UserProfileResponse response = userService.registerProfile(
                authUser.getAccountId(),
                request.getName(),
                request.getPhone()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(response));
    }

    @Override
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommonResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal AuthUser authUser) {

        UserProfileResponse response = userService.getMyProfile(authUser.getAccountId());

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommonResponse<UserProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UpdateUserRequest request) {

        UserProfileResponse response = userService.updateMyProfile(
                authUser.getAccountId(),
                request.getName(),
                request.getPhone()
        );

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMyProfile(
            @AuthenticationPrincipal AuthUser authUser) {

        userService.deleteMyProfile(authUser.getAccountId());

        return ResponseEntity.noContent().build();
    }
}
