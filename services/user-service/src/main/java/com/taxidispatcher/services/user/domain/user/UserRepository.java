package com.taxidispatcher.services.user.domain.user;

import java.util.Optional;

/**
 * 사용자 저장소 인터페이스 (포트)
 * Infrastructure 계층에서 구현
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(UserId userId);

    Optional<User> findByAccountId(String accountId);

    boolean existsByAccountId(String accountId);

    void delete(User user);
}
