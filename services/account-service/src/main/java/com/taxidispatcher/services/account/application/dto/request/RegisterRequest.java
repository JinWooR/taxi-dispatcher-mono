package com.taxidispatcher.services.account.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO
 * 비밀번호 일치 확인은 클라이언트에서 처리하고, API는 단일 비밀번호만 수신합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "로그인 ID는 필수입니다")
    @Email(message = "올바른 이메일 형식이어야 합니다")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, max = 30, message = "비밀번호는 6~30자여야 합니다")
    private String password;
}
