package com.taxidispatcher.services.driver.presentation;

import com.taxidispatcher.services.driver.application.dto.request.ChangeStatusRequest;
import com.taxidispatcher.services.driver.application.dto.request.RegisterDriverRequest;
import com.taxidispatcher.services.driver.application.dto.request.UpdateDriverRequest;
import com.taxidispatcher.services.driver.application.dto.response.DriverResponse;
import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import com.taxidispatcher.shared.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Driver API", description = "기사 관련 API")
@SecurityRequirement(name = BaseOpenApiConfig.ACCESS_TOKEN_SCHEME)
public interface DriverApi {

    @Operation(summary = "기사 등록", description = "새로운 기사를 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공", content = @Content(schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "중복된 기사 또는 면허번호"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<DriverResponse>> register(@Valid @RequestBody RegisterDriverRequest request);

    @Operation(summary = "내 정보 조회", description = "기사의 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "기사를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<DriverResponse>> getMe();

    @Operation(summary = "프로필 수정", description = "기사의 프로필을 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "기사를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "중복된 면허번호"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<DriverResponse>> updateProfile(@Valid @RequestBody UpdateDriverRequest request);

    @Operation(summary = "상태 변경", description = "기사의 상태를 변경합니다 (OFFLINE, ONLINE, BUSY)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "기사를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<Void>> changeStatus(@Valid @RequestBody ChangeStatusRequest request);
}
