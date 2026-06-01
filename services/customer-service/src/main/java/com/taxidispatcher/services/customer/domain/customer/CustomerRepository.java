package com.taxidispatcher.services.customer.domain.customer;

import java.util.Optional;

/**
 * 사용자 저장소 인터페이스 (포트)
 * Infrastructure 계층에서 구현
 */
public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(CustomerId customerId);

    Optional<Customer> findByAccountId(String accountId);

    boolean existsByAccountId(String accountId);

    void delete(Customer customer);
}
