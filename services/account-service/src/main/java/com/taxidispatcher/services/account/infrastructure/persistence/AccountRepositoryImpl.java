package com.taxidispatcher.services.account.infrastructure.persistence;

import com.taxidispatcher.services.account.domain.account.Account;
import com.taxidispatcher.services.account.domain.account.AccountId;
import com.taxidispatcher.services.account.domain.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Account Repository 구현체 (JPA 기반)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository jpaRepository;
    private final BasicCredentialJpaRepository basicCredentialJpaRepository;
    private final OAuthCredentialJpaRepository oAuthCredentialJpaRepository;

    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = AccountJpaEntity.fromDomain(account);

        AccountJpaEntity saved;
        Optional<AccountJpaEntity> existing = jpaRepository.findById(account.getAccountId().getValue());
        if (existing.isPresent()) {
            existing.get().updateFromDomain(account);
            saved = jpaRepository.save(existing.get());
        } else {
            saved = jpaRepository.save(entity);
        }

        return saved.toDomain();
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        return jpaRepository.findById(accountId.getValue())
                .map(AccountJpaEntity::toDomain);
    }

    @Override
    public Optional<Account> findByLoginId(String loginId) {
        return basicCredentialJpaRepository.findByLoginId(loginId)
                .flatMap(credential -> jpaRepository.findById(credential.getAccountId()))
                .map(AccountJpaEntity::toDomain);
    }

    @Override
    public boolean existsByAccountId(AccountId accountId) {
        return jpaRepository.existsById(accountId.getValue());
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return basicCredentialJpaRepository.existsByLoginId(loginId);
    }
}
