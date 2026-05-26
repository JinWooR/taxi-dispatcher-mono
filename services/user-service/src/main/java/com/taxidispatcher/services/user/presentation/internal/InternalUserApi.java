package com.taxidispatcher.services.user.presentation.internal;

import com.taxidispatcher.services.user.application.dto.response.UserProfileResponse;
import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import com.taxidispatcher.shared.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 사용자 내부 API (서비스 간 통신 전용)
 * Authorization: ApiKey {USER_INTERNAL_API_KEY} 필요
 */
@Tag(name = "Internal User API", description = "서비스 간 통신용 내부 API")
@SecurityRequirement(name = BaseOpenApiConfig.API_KEY_SCHEME)
public interface InternalUserApi {

    @Operation(summary = "accountId로 사용자 프로필 조회",
            description = "계정 ID로 사용자 프로필 정보를 조회합니다. 서비스 간 통신용")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 API Key"),
            @ApiResponse(responseCode = "404", description = "프로필 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<UserProfileResponse>> findByAccountId(@PathVariable String accountId);
}
