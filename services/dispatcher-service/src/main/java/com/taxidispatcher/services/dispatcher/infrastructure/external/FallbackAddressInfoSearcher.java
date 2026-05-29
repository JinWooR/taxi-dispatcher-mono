package com.taxidispatcher.services.dispatcher.infrastructure.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * 카카오 API 우선 호출, 실패 시 네이버로 fallback
 */
@Slf4j
@Service
@Primary
public class FallbackAddressInfoSearcher implements AddressInfoSearcher {

    private final AddressInfoSearcher primary;
    private final AddressInfoSearcher fallback;

    public FallbackAddressInfoSearcher(
            @Qualifier("kakao") AddressInfoSearcher primary,
            @Qualifier("naver") AddressInfoSearcher fallback) {
        this.primary = primary;
        this.fallback = fallback;
    }

    @Override
    public AddressInfo search(String address) {
        try {
            return primary.search(address);
        } catch (Exception e) {
            log.warn("카카오 주소 검색 실패, 네이버로 fallback: address={}", address, e);
            return fallback.search(address);
        }
    }
}
