package com.taxidispatcher.services.dispatcher.application.dto.response;

import com.taxidispatcher.services.dispatcher.domain.dispatch.Dispatch;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DispatchResponse {
    // 배차 ID
    private String dispatchId;
    // 배차 상태
    private DispatchStatus dispatchStatus;
    // 기사 ID
    private String driverId;
    // 출발지 위도
    private double departureLatitude;
    // 출발지 경도
    private double departureLongitude;
    // 출발지 주소
    private String departureAddress;
    // 도착지 위도
    private double arrivalLatitude;
    // 도착지 경도
    private double arrivalLongitude;
    // 도착지 주소
    private String arrivalAddress;
    // 배차 요청 시간
    private LocalDateTime requestedAt;
    // 배차 실패 시간
    private LocalDateTime failedAt;
    // 배차 승인 시간
    private LocalDateTime approvedAt;
    // 출발 시간
    private LocalDateTime departedAt;
    // 목적지 도착 시간
    private LocalDateTime arrivedAt;
    // 배차 완료 시간
    private LocalDateTime completedAt;
    // 현재 탐색 범위 (km)
    private int currentSearchScope;

    public static DispatchResponse from(Dispatch dispatch) {
        return new DispatchResponse(
            dispatch.getDispatchId().getValue(),
            dispatch.getDispatchStatus(),
            dispatch.getDriverId() != null ? dispatch.getDriverId().getValue() : null,
            dispatch.getDeparture().getLatitude(),
            dispatch.getDeparture().getLongitude(),
            dispatch.getDeparture().getAddress(),
            dispatch.getArrival().getLatitude(),
            dispatch.getArrival().getLongitude(),
            dispatch.getArrival().getAddress(),
            dispatch.getRequestedAt(),
            dispatch.getFailedAt(),
            dispatch.getApprovedAt(),
            dispatch.getDepartedAt(),
            dispatch.getArrivedAt(),
            dispatch.getCompletedAt(),
            dispatch.getSearchScope().getCurrentRadiusKm()
        );
    }
}
