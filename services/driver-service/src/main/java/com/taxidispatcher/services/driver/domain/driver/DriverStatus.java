package com.taxidispatcher.services.driver.domain.driver;

public enum DriverStatus {
    OFFLINE,  // 오프라인
    ONLINE,   // 온라인 (배차 가능)
    BUSY      // 배차 중 (다른 기사 배차 불가)
}
