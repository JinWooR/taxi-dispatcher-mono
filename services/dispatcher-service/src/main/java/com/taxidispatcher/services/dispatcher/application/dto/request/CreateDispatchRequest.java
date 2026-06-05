package com.taxidispatcher.services.dispatcher.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDispatchRequest {

    @Schema(description = "출발지 위도", example = "37.5665",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private double departureLatitude;

    @Schema(description = "출발지 경도", example = "126.9780",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private double departureLongitude;

    @Schema(description = "출발지 주소", example = "서울특별시 중구 세종대로 110",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String departureAddress;

    @Schema(description = "도착지 위도", example = "37.5172",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private double arrivalLatitude;

    @Schema(description = "도착지 경도", example = "127.0473",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private double arrivalLongitude;

    @Schema(description = "도착지 주소", example = "서울특별시 강남구 테헤란로 521",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String arrivalAddress;
}
