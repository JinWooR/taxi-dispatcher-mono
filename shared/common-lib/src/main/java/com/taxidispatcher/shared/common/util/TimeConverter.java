package com.taxidispatcher.shared.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * API 계층의 {@link Instant}와 내부 {@link LocalDateTime}(UTC) 간 변환 유틸.
 *
 * <p>프로젝트 시간 규칙
 * <ul>
 *   <li>API 입출력: {@code Instant} (UTC 절대 시각, ISO 8601)</li>
 *   <li>Application/Domain/JPA: {@code LocalDateTime} (UTC 기준)</li>
 * </ul>
 *
 * <p>null-in / null-out 정책. 호출 측에서 별도 분기 없이 사용 가능.
 */
public final class TimeConverter {

    private TimeConverter() {
    }

    public static LocalDateTime toUtcLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public static Instant toInstant(LocalDateTime utcLocalDateTime) {
        if (utcLocalDateTime == null) {
            return null;
        }
        return utcLocalDateTime.toInstant(ZoneOffset.UTC);
    }
}
