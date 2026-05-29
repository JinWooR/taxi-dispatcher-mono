package com.taxidispatcher.services.dispatcher.infrastructure.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taxidispatcher.shared.common.exception.DomainException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * <a href="https://api.ncloud-docs.com/docs/ai-naver-mapsgeocoding-geocode">네이버 API 문서</a>
 */
@Service
@Qualifier("naver")
public class NaverAddressInfoSearcher implements AddressInfoSearcher {

    private final String clientId;
    private final String clientSecret;
    private static final String API_BASE_URL = "https://naveropenapi.apigw.ntruss.com";
    private static final String API_URI = "/map-geocode/v2/geocode";

    public NaverAddressInfoSearcher(
            @Value("${naver.client-id}") String clientId,
            @Value("${naver.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public AddressInfo search(String address) {
        GeocodeResponse res = RestClient.create(API_BASE_URL)
                .get()
                .uri(uriAddParams(address))
                .header("X-NCP-APIGW-API-KEY-ID", clientId)
                .header("X-NCP-APIGW-API-KEY", clientSecret)
                .header("Accept", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new IllegalArgumentException("네이버 API 잘못된 요청");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new IllegalStateException("네이버 API 외부 서비스 호출 오류");
                })
                .body(GeocodeResponse.class);

        if (res == null || res.getAddresses() == null || res.getAddresses().isEmpty()) {
            throw new DomainException("ADDRESS_NOT_FOUND",
                    "네이버 주소 정보를 확인할 수 없습니다: " + address, HttpStatus.NOT_FOUND);
        }

        var addr = res.getAddresses().get(0);
        String sd = null, sgg = null, emd = null;
        for (var element : addr.getAddressElements()) {
            if (element.getTypes() == null || element.getTypes().isEmpty()) continue;
            String type = element.getTypes().get(0);
            switch (type) {
                case "SIDO" -> sd = element.getLongName();
                case "SIGUGUN" -> sgg = element.getLongName();
                case "DONGMYUN" -> emd = element.getLongName();
            }
        }
        return new AddressInfo(sd, sgg, emd);
    }

    private String uriAddParams(String address) {
        return UriComponentsBuilder.fromPath(API_URI)
                .queryParam("query", address)
                .queryParam("page", 1)
                .queryParam("count", 1)
                .build()
                .toUriString();
    }

    @Getter
    public static class GeocodeResponse {
        @JsonProperty
        private List<Address> addresses;

        @Getter
        public static class Address {
            @JsonProperty("addressElements")
            private List<AddressElement> addressElements;
        }

        @Getter
        public static class AddressElement {
            @JsonProperty("types")
            private List<String> types;
            @JsonProperty("longName")
            private String longName;
        }
    }
}
