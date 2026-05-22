package com.taxidispatcher.services.account.infrastructure.persistence;

import com.taxidispatcher.services.account.domain.account.AccountId;
import com.taxidispatcher.services.account.domain.token.RefreshToken;
import com.taxidispatcher.services.account.domain.token.RefreshTokenRepository;
import com.taxidispatcher.services.account.domain.token.TokenId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return jpaRepository.save(RefreshTokenJpaEntity.fromDomain(refreshToken)).toDomain();
    }

    @Override
    public Optional<RefreshToken> findByTokenId(TokenId tokenId) {
        return jpaRepository.findById(tokenId.getValue())
                .map(RefreshTokenJpaEntity::toDomain);
    }

    @Override
    public void deleteByTokenId(TokenId tokenId) {
        jpaRepository.deleteById(tokenId.getValue());
    }

    @Override
    public void deleteAllByAccountId(AccountId accountId) {
        jpaRepository.deleteAllByAccountId(accountId.getValue());
    }
}
