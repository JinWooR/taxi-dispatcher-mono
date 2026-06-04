package com.taxidispatcher.services.dispatcher.infrastructure.util;

import com.taxidispatcher.shared.common.request.PageableRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * PageableRequest → Spring Data Pageable 변환 유틸리티
 *
 * <p>정렬 표현: {@code field,direction} (예: {@code requestedAt,desc})
 * <p>direction 미지정 시 ASC 기본값
 * <p>화이트리스트 검증은 PageableRequest의 @AssertTrue로 Bean Validation에서 수행됨
 */
public final class PageableConverter {

    private PageableConverter() {
    }

    public static Pageable toPageable(PageableRequest request) {
        if (request == null) {
            return PageRequest.of(0, 20);
        }

        List<String> sortTokens = request.getSort();
        if (sortTokens == null || sortTokens.isEmpty()) {
            return PageRequest.of(request.getPage(), request.getSize());
        }

        List<Sort.Order> orders = sortTokens.stream()
                .filter(token -> token != null && !token.isBlank())
                .map(PageableConverter::parseOrder)
                .toList();

        return orders.isEmpty()
                ? PageRequest.of(request.getPage(), request.getSize())
                : PageRequest.of(request.getPage(), request.getSize(), Sort.by(orders));
    }

    private static Sort.Order parseOrder(String token) {
        String[] parts = token.split(",", 2);
        String field = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return new Sort.Order(direction, field);
    }
}
