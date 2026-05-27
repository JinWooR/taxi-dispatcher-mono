package com.taxidispatcher.services.dispatcher.domain.dispatch;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public class SearchScope {
    private static final List<Integer> SCOPE_RADIUS_KM = Arrays.asList(1, 3, 5);
    private static final int SCOPE_EXPIRY_SECONDS = 30;

    private int currentScope; // 1, 2, 3 (범위 단계)
    private LocalDateTime scopeStartedAt;

    public SearchScope() {
        this.currentScope = 1;
        this.scopeStartedAt = LocalDateTime.now();
    }

    // DB 복원용
    public static SearchScope reconstitute(int currentScope, LocalDateTime scopeStartedAt) {
        SearchScope scope = new SearchScope();
        scope.currentScope = currentScope;
        scope.scopeStartedAt = scopeStartedAt;
        return scope;
    }

    public void expandScope() {
        if (currentScope < SCOPE_RADIUS_KM.size()) {
            currentScope++;
            scopeStartedAt = LocalDateTime.now();
        }
    }

    public int getCurrentRadiusKm() {
        return SCOPE_RADIUS_KM.get(currentScope - 1);
    }

    public boolean isExpired() {
        LocalDateTime expiryTime = scopeStartedAt.plusSeconds(SCOPE_EXPIRY_SECONDS);
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public boolean hasNextScope() {
        return currentScope < SCOPE_RADIUS_KM.size();
    }

    public static int getScopeExpirySeconds() {
        return SCOPE_EXPIRY_SECONDS;
    }

    public static List<Integer> getScopeRadiusKm() {
        return SCOPE_RADIUS_KM;
    }
}
