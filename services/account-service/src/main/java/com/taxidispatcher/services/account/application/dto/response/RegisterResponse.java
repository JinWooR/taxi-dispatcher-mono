package com.taxidispatcher.services.account.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taxidispatcher.services.account.domain.account.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원가입 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    private String accountId;

    private String loginId;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime createdAt;

    /**
     * 도메인에서 응답 DTO로 변환
     */
    public static RegisterResponse from(Account account) {
        String loginId = account.getCredentials().stream()
                .filter(c -> c.getType().equals("BASIC"))
                .map(c -> {
                    try {
                        return (String) c.getClass().getMethod("getLoginId").invoke(c);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(id -> id != null)
                .findFirst()
                .orElse(null);

        return RegisterResponse.builder()
                .accountId(account.getAccountId().getValue())
                .loginId(loginId)
                .status(account.getStatus().name())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
