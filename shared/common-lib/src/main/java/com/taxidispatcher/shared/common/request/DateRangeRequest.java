package com.taxidispatcher.shared.common.request;

import com.taxidispatcher.shared.common.util.TimeConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 공통 날짜 범위 검색 요청 DTO
 *
 * <p>시간대: UTC 절대 시각 ({@link Instant}, ISO 8601)
 * <p>클라이언트는 어떤 시간대 입력이든 가능 (예: {@code "2026-06-04T14:30:00+09:00"}, {@code "...Z"}).
 * <p>모든 필드 nullable. 클라이언트가 필요한 조합만 전달.
 */
@Getter
@Setter
@NoArgsConstructor
public class DateRangeRequest {

    @Schema(
            description = "검색 시작 시각 (UTC 절대 시각, ISO 8601). 시간대 표기는 자유 (예: `Z`, `+09:00`).",
            example = "2026-06-01T00:00:00Z"
    )
    private Instant startDate;

    @Schema(
            description = "검색 종료 시각 (UTC 절대 시각, ISO 8601).",
            example = "2026-06-30T23:59:59Z"
    )
    private Instant endDate;

    /**
     * {@link #startDate}를 내부 도메인/JPA 표준인 UTC {@link LocalDateTime}으로 변환.
     */
    public LocalDateTime toStartLocalDateTime() {
        return TimeConverter.toUtcLocalDateTime(startDate);
    }

    /**
     * {@link #endDate}를 내부 도메인/JPA 표준인 UTC {@link LocalDateTime}으로 변환.
     */
    public LocalDateTime toEndLocalDateTime() {
        return TimeConverter.toUtcLocalDateTime(endDate);
    }
}
