package com.taxidispatcher.services.customer.application.service;

import com.taxidispatcher.services.customer.application.dto.response.CustomerProfileResponse;
import com.taxidispatcher.services.customer.domain.customer.Customer;
import com.taxidispatcher.services.customer.domain.customer.CustomerId;
import com.taxidispatcher.services.customer.domain.customer.CustomerRepository;
import com.taxidispatcher.shared.common.exception.DomainException;
import com.taxidispatcher.shared.common.dto.customer.internal.CustomerInternalProfile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * 사용자 프로필 비즈니스 로직
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerProfileResponse registerProfile(String accountId, String name, String phone) {
        if (customerRepository.existsByAccountId(accountId)) {
            throw new DomainException("USER_DUPLICATE_ACCOUNT", "이미 등록된 프로필입니다", HttpStatus.CONFLICT);
        }

        Customer customer = new Customer(CustomerId.generate(), accountId, name, phone);
        Customer saved = customerRepository.save(customer);

        return CustomerProfileResponse.from(saved);
    }

    public CustomerProfileResponse getMyProfile(String accountId) {
        Customer customer = customerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "프로필을 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        return CustomerProfileResponse.from(customer);
    }

    /**
     * 내부 API: accountId로 프로필 조회 (서비스 간 통신용)
     */
    public CustomerInternalProfile findProfileByAccountId(String accountId) {
        Customer customer = customerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "프로필을 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        return CustomerInternalProfile.builder()
                .customerId(customer.getCustomerId().getValue())
                .accountId(customer.getAccountId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .status(customer.getStatus().name())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    public CustomerProfileResponse updateMyProfile(String accountId, String name, String phone) {
        Customer customer = customerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "프로필을 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        customer.update(name, phone);
        Customer updated = customerRepository.save(customer);

        return CustomerProfileResponse.from(updated);
    }

    public void deleteMyProfile(String accountId) {
        Customer customer = customerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "프로필을 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        customer.delete();
        customerRepository.save(customer);
    }
}
