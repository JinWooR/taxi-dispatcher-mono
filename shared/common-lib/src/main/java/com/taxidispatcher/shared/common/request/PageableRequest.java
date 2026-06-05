package com.taxidispatcher.shared.common.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * 공통 페이지네이션/정렬 요청 DTO (추상 클래스)
 *
 * <p>정렬 표현: {@code field,direction} (예: {@code requestedAt,desc})
 * <p>Spring Data 의존성을 가지지 않는 순수 DTO. 각 서비스의 PageableConverter에서 Pageable로 변환.
 *
 * <p>각 API별로 본 클래스를 상속하여 {@link #allowedSortFields()}를 구현해야 한다.
 * 정렬 화이트리스트 검증은 {@link #isValidSort()}를 통해 Bean Validation으로 자동 수행된다.
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class PageableRequest {

    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(100)
    private int size = 20;

    private List<String> sort;

    /**
     * API별 허용 정렬 필드 정의 (자식 클래스 구현 필수)
     */
    protected abstract Set<String> allowedSortFields();

    /**
     * 정렬 필드 화이트리스트 검증 (Bean Validation)
     * <p>{@code sort=field,direction} 토큰의 필드명이 {@link #allowedSortFields()}에 포함되어야 한다.
     */
    @AssertTrue(message = "허용된 정렬 필드만 사용 가능합니다")
    @JsonIgnore
    public boolean isValidSort() {
        if (sort == null || sort.isEmpty()) {
            return true;
        }
        Set<String> allowed = allowedSortFields();
        return sort.stream()
                .filter(token -> token != null && !token.isBlank())
                .map(token -> token.split(",", 2)[0].trim())
                .allMatch(allowed::contains);
    }
}
