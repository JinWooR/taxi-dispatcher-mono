package com.taxidispatcher.services.customer.presentation.internal;

import com.taxidispatcher.services.customer.application.service.CustomerService;
import com.taxidispatcher.shared.common.dto.customer.internal.CustomerInternalProfile;
import com.taxidispatcher.shared.common.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/customers")
public class InternalCustomerController implements InternalCustomerApi {

    private final CustomerService customerService;

    public InternalCustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<CommonResponse<CustomerInternalProfile>> findByAccountId(@PathVariable String accountId) {
        CustomerInternalProfile profile = customerService.findProfileByAccountId(accountId);
        return ResponseEntity.ok(CommonResponse.success(profile));
    }
}
