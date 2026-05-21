package com.taxidispatcher.services.account.infrastructure.persistence;

import com.taxidispatcher.services.account.domain.credential.OAuthKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthCredentialJpaRepository extends JpaRepository<OAuthCredentialJpaEntity, Long> {

    Optional<OAuthCredentialJpaEntity> findByOauthKindAndSub(OAuthKind oauthKind, String sub);

    boolean existsByOauthKindAndSub(OAuthKind oauthKind, String sub);
}
