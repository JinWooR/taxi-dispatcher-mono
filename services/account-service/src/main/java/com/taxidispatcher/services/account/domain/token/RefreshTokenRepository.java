package com.taxidispatcher.services.account.domain.token;

import com.taxidispatcher.services.account.domain.account.AccountId;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByTokenId(TokenId tokenId);

    void deleteByTokenId(TokenId tokenId);

    void deleteAllByAccountId(AccountId accountId);
}
