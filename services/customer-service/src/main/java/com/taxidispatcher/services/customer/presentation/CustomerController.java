package com.taxidispatcher.services.customer.presentation;

import com.taxidispatcher.services.customer.application.dto.request.RegisterCustomerRequest;
import com.taxidispatcher.services.customer.application.dto.request.UpdateCustomerRequest;
import com.taxidispatcher.services.customer.application.dto.response.CustomerProfileResponse;
import com.taxidispatcher.services.customer.application.service.CustomerService;
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
@RequestMapping("/customers")
@Validated
public class CustomerController implements CustomerApi {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('NONE', 'USER')")
    public ResponseEntity<CommonResponse<CustomerProfileResponse>> registerProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody RegisterCustomerRequest request) {

        CustomerProfileResponse response = customerService.registerProfile(
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
    public ResponseEntity<CommonResponse<CustomerProfileResponse>> getMyProfile(
            @AuthenticationPrincipal AuthUser authUser) {

        CustomerProfileResponse response = customerService.getMyProfile(authUser.getAccountId());

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommonResponse<CustomerProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UpdateCustomerRequest request) {

        CustomerProfileResponse response = customerService.updateMyProfile(
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

        customerService.deleteMyProfile(authUser.getAccountId());

        return ResponseEntity.noContent().build();
    }
}
