package com.taxidispatcher.services.dispatcher.application.service;

import com.taxidispatcher.services.dispatcher.application.dto.request.CreateDispatchRequest;
import com.taxidispatcher.services.dispatcher.application.dto.request.UpdateDispatchStatusRequest;
import com.taxidispatcher.services.dispatcher.application.dto.response.DispatchResponse;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DispatchService {
    // 배차 요청 생성
    DispatchResponse createDispatch(String userId, CreateDispatchRequest request);

    // 사용자의 배차 목록 조회
    Page<DispatchResponse> getDispatchesByUser(String userId, Pageable pageable);

    // 기사의 pending 배차 목록 조회
    Page<DispatchResponse> getPendingDispatches(Pageable pageable);

    // 배차 상세 조회
    DispatchResponse getDispatch(String dispatchId);

    // 배차 승인
    DispatchResponse acceptDispatch(String dispatchId, String driverId);

    // 배차 거절
    DispatchResponse rejectDispatch(String dispatchId, String driverId);

    // 배차 상태 변경
    DispatchResponse updateDispatchStatus(String dispatchId, UpdateDispatchStatusRequest request);
}
