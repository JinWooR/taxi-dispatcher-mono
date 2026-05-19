package com.taxidispatcher.services.user.infrastructure.persistence;

import com.taxidispatcher.services.user.domain.user.User;
import com.taxidispatcher.services.user.domain.user.UserId;
import com.taxidispatcher.services.user.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository 구현체 (Clean Architecture Adapter)
 * Spring Data JPA를 Domain Repository 인터페이스로 적응
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.from(user);
        UserJpaEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toModel();
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return jpaRepository.findById(userId.getValue())
                .map(UserJpaEntity::toModel);
    }

    @Override
    public Optional<User> findByAccountId(String accountId) {
        return jpaRepository.findByAccountId(accountId)
                .map(UserJpaEntity::toModel);
    }

    @Override
    public boolean existsByAccountId(String accountId) {
        return jpaRepository.existsByAccountId(accountId);
    }

    @Override
    public void delete(User user) {
        UserJpaEntity entity = UserJpaEntity.from(user);
        jpaRepository.delete(entity);
    }
}
