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

    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = AccountJpaEntity.fromDomain(account);

        // 기존 엔티티가 있으면 업데이트
        Optional<AccountJpaEntity> existing = jpaRepository.findByAccountId(account.getAccountId().getValue());

        AccountJpaEntity saved;
        if (existing.isPresent()) {
            AccountJpaEntity existingEntity = existing.get();
            existingEntity.updateFromDomain(account);
            saved = jpaRepository.save(existingEntity);
        } else {
            saved = jpaRepository.save(entity);
        }

        // Credential 저장 (필요시)
        account.getCredentials().forEach(credential -> {
            // JPA에서 자동 저장됨 (Cascade)
        });

        return saved.toDomain();
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        return jpaRepository.findByAccountId(accountId.getValue())
                .map(AccountJpaEntity::toDomain);
    }

    @Override
    public Optional<Account> findByLoginId(String loginId) {
        return jpaRepository.findByLoginId(loginId)
                .map(AccountJpaEntity::toDomain);
    }

    @Override
    public boolean existsByAccountId(AccountId accountId) {
        return jpaRepository.existsByAccountId(accountId.getValue());
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return jpaRepository.existsByLoginId(loginId);
    }
}
