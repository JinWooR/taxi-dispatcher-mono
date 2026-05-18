package com.taxidispatcher.shared.common.response;

import lombok.Getter;

import java.util.List;

/**
 * 공통 페이지 응답 래퍼
 * ApiResponse<PagedResponse<T>> 형태로 사용
 *
 * <pre>
 * // 서비스에서 사용 예시 (Spring Data JPA Page 변환)
 * Page<Entity> page = repository.findAll(pageable);
 * PagedResponse<ResponseDto> response = PagedResponse.of(
 *     page.getContent().stream().map(ResponseDto::from).toList(),
 *     PageInfo.of(page.getNumber(), page.getSize(), page.getTotalElements(),
 *                 page.getTotalPages(), page.isFirst(), page.isLast(),
 *                 page.hasNext(), page.hasPrevious())
 * );
 * return ResponseEntity.ok(ApiResponse.success(response, "조회 성공"));
 * </pre>
 */
@Getter
public class PagedResponse<T> {

    private final List<T> content;
    private final PageInfo pageInfo;

    private PagedResponse(List<T> content, PageInfo pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

    public static <T> PagedResponse<T> of(List<T> content, PageInfo pageInfo) {
        return new PagedResponse<>(content, pageInfo);
    }
}
