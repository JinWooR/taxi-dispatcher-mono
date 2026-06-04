package com.taxidispatcher.shared.common.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 공통 페이지네이션/정렬 요청 DTO
 *
 * <p>정렬 표현: {@code field,direction} (예: {@code requestedAt,desc})
 * <p>Spring Data 의존성을 가지지 않는 순수 DTO. 각 서비스의 PageableConverter에서 Pageable로 변환.
 */
@Getter
@Setter
@NoArgsConstructor
public class PageableRequest {

    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(100)
    private int size = 20;

    private List<String> sort;
}
