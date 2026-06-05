package com.taxidispatcher.services.dispatcher.application.dto.request;

import com.taxidispatcher.shared.common.request.PageableRequest;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/**
 * 기사 Pending 배차 목록 조회용 PageableRequest
 * <p>허용 정렬 필드: requestedAt, scopeStartedAt
 */
@Schema(description = "기사 Pending 배차 목록 페이지네이션 요청. " +
        "허용 정렬 필드: `requestedAt`, `scopeStartedAt`")
public class DriverPendingPageRequest extends PageableRequest {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("requestedAt", "scopeStartedAt");

    @Override
    protected Set<String> allowedSortFields() {
        return ALLOWED_SORT_FIELDS;
    }
}
