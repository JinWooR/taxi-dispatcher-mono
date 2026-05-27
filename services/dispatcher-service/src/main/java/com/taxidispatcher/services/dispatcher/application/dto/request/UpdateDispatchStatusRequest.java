package com.taxidispatcher.services.dispatcher.application.dto.request;

import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDispatchStatusRequest {
    // 변경할 배차 상태
    private DispatchStatus status;
}
