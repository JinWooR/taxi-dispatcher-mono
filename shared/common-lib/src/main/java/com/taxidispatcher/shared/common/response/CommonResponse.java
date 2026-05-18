package com.taxidispatcher.shared.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 공통 API 응답 래퍼
 * <p>
 * 성공: { code, message, data, timestamp }
 * 에러: { code, message, timestamp, path }
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
    private final String path;  // 에러 응답 시 요청 경로 (GlobalExceptionHandler에서 채움)

    private CommonResponse(String code, String message, T data, String path) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = FORMATTER.format(Instant.now());
        this.path = path;
    }

    // 성공 응답
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>("SUCCESS", "요청 성공", data, null);
    }

    public static <T> CommonResponse<T> success(T data, String message) {
        return new CommonResponse<>("SUCCESS", message, data, null);
    }

    // 에러 응답
    public static <T> CommonResponse<T> error(String code, String message) {
        return new CommonResponse<>(code, message, null, null);
    }

    public static <T> CommonResponse<T> error(String code, String message, String path) {
        return new CommonResponse<>(code, message, null, path);
    }
}
