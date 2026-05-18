package com.taxidispatcher.shared.common.response;

import lombok.Getter;

/**
 * 공통 페이지네이션 정보
 * 각 서비스에서 Page<T>를 이 클래스로 변환하여 사용
 */
@Getter
public class PageInfo {

    private final int currentPage;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    private final boolean isFirst;
    private final boolean isLast;
    private final boolean hasNext;
    private final boolean hasPrevious;

    private PageInfo(int currentPage, int pageSize, long totalElements, int totalPages,
                     boolean isFirst, boolean isLast, boolean hasNext, boolean hasPrevious) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public static PageInfo of(int currentPage, int pageSize, long totalElements, int totalPages,
                               boolean isFirst, boolean isLast, boolean hasNext, boolean hasPrevious) {
        return new PageInfo(currentPage, pageSize, totalElements, totalPages,
                isFirst, isLast, hasNext, hasPrevious);
    }
}
