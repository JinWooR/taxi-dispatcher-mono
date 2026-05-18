package com.taxidispatcher.shared.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 공통 API 응답 래퍼
 * <p>
 * 성공: { code, message, data, timestamp }
 * 에러: { code, message, timestamp, path }
 * 검증 실패: { code, message, timestamp, errors }
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .withZone(ZoneId.of("UTC"));

    private final String code;
    private final String message;
    private final T data;
    private final String timestamp;
    private final String path;      // 에러 응답 시 요청 경로 (GlobalExceptionHandler에서 채움)
    private final List<FieldError> errors;  // 검증 실패 시 필드별 오류

    private CommonResponse(String code, String message, T data, String path, List<FieldError> errors) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = FORMATTER.format(Instant.now());
        this.path = path;
        this.errors = errors;
    }

    // 성공 응답
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>("SUCCESS", "요청 성공", data, null, null);
    }

    public static <T> CommonResponse<T> success(T data, String message) {
        return new CommonResponse<>("SUCCESS", message, data, null, null);
    }

    // 에러 응답
    public static <T> CommonResponse<T> error(String code, String message) {
        return new CommonResponse<>(code, message, null, null, null);
    }

    public static <T> CommonResponse<T> error(String code, String message, String path) {
        return new CommonResponse<>(code, message, null, path, null);
    }

    // 검증 실패 응답
    public static <T> CommonResponse<T> validationError(String message, List<FieldError> errors) {
        return new CommonResponse<>("INVALID_REQUEST", message, null, null, errors);
    }

    /**
     * 필드 검증 오류
     */
    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private final String field;
        private final String message;
    }
}
