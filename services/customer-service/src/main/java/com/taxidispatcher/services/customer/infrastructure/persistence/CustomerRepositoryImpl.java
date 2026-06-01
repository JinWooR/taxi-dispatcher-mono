package com.taxidispatcher.services.customer.infrastructure.persistence;

import com.taxidispatcher.services.customer.domain.customer.Customer;
import com.taxidispatcher.services.customer.domain.customer.CustomerId;
import com.taxidispatcher.services.customer.domain.customer.CustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CustomerRepository 구현체 (Clean Architecture Adapter)
 * Spring Data JPA를 Domain Repository 인터페이스로 적응
 */
@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;

    public CustomerRepositoryImpl(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity entity = CustomerJpaEntity.from(customer);
        CustomerJpaEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toModel();
    }

    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        return jpaRepository.findById(customerId.getValue())
                .map(CustomerJpaEntity::toModel);
    }

    @Override
    public Optional<Customer> findByAccountId(String accountId) {
        return jpaRepository.findByAccountId(accountId)
                .map(CustomerJpaEntity::toModel);
    }

    @Override
    public boolean existsByAccountId(String accountId) {
        return jpaRepository.existsByAccountId(accountId);
    }

    @Override
    public void delete(Customer customer) {
        CustomerJpaEntity entity = CustomerJpaEntity.from(customer);
        jpaRepository.delete(entity);
    }
}
