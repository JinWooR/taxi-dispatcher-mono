package com.taxidispatcher.services.dispatcher.application.dto.request;

import com.taxidispatcher.shared.common.request.PageableRequest;

import java.util.Set;

/**
 * 고객 배차 목록 조회용 PageableRequest
 * <p>허용 정렬 필드: requestedAt, dispatchStatus
 */
public class CustomerDispatchPageRequest extends PageableRequest {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("requestedAt", "dispatchStatus");

    @Override
    protected Set<String> allowedSortFields() {
        return ALLOWED_SORT_FIELDS;
    }
}
