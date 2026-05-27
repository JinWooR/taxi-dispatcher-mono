package com.taxidispatcher.services.dispatcher.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDispatchRequest {
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
}
