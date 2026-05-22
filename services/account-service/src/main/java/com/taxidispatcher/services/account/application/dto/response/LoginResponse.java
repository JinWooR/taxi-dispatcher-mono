package com.taxidispatcher.services.account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accountId;
    private String role;
    private String actor;
    private String credentialId;
    private TokenInfo token;
}
