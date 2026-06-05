package com.taxidispatcher.services.dispatcher.application.dto.response;

import com.taxidispatcher.services.dispatcher.domain.dispatch.Dispatch;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DispatchResponse {

    @Schema(description = "배차 ID (UUID)", example = "990e8400-e29b-41d4-a716-446655440004")
    private String dispatchId;

    @Schema(description = "배차 상태", example = "PENDING")
    private DispatchStatus dispatchStatus;

    @Schema(description = "배정된 기사 ID (UUID). 미배정 시 null", example = "770e8400-e29b-41d4-a716-446655440002")
    private String driverId;

    @Schema(description = "출발지 위도", example = "37.5665")
    private double departureLatitude;

    @Schema(description = "출발지 경도", example = "126.9780")
    private double departureLongitude;

    @Schema(description = "출발지 주소", example = "서울특별시 중구 세종대로 110")
    private String departureAddress;

    @Schema(description = "도착지 위도", example = "37.5172")
    private double arrivalLatitude;

    @Schema(description = "도착지 경도", example = "127.0473")
    private double arrivalLongitude;

    @Schema(description = "도착지 주소", example = "서울특별시 강남구 테헤란로 521")
    private String arrivalAddress;

    @Schema(description = "배차 요청 시각 (UTC, ISO 8601)", example = "2026-06-04T05:30:00Z")
    private LocalDateTime requestedAt;

    @Schema(description = "배차 실패 시각 (UTC, ISO 8601). 실패 전 null", example = "2026-06-04T05:32:00Z")
    private LocalDateTime failedAt;

    @Schema(description = "배차 승인 시각 (UTC, ISO 8601). 승인 전 null", example = "2026-06-04T05:31:00Z")
    private LocalDateTime approvedAt;

    @Schema(description = "출발 시각 (UTC, ISO 8601)", example = "2026-06-04T05:35:00Z")
    private LocalDateTime departedAt;

    @Schema(description = "목적지 도착 시각 (UTC, ISO 8601)", example = "2026-06-04T06:00:00Z")
    private LocalDateTime arrivedAt;

    @Schema(description = "배차 완료 시각 (UTC, ISO 8601)", example = "2026-06-04T06:05:00Z")
    private LocalDateTime completedAt;

    @Schema(description = "현재 기사 탐색 반경 (km)", example = "3")
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
