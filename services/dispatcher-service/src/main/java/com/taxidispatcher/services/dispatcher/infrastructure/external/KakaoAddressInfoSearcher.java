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
import java.util.Optional;

/**
 * <a href="https://developers.kakao.com/docs/latest/ko/local/dev-guide#address-coord">카카오 API 문서</a>
 */
@Service
@Qualifier("kakao")
public class KakaoAddressInfoSearcher implements AddressInfoSearcher {

    private final String apiKey;
    private static final String API_BASE_URL = "https://dapi.kakao.com";
    private static final String API_URI = "/v2/local/search/address.json";

    public KakaoAddressInfoSearcher(@Value("${kakao.rest-api}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public AddressInfo search(String address) {
        LocalAddressResponse res = RestClient.create(API_BASE_URL)
                .get()
                .uri(uriAddParams(address))
                .header("Authorization", "KakaoAK " + apiKey)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new IllegalArgumentException("카카오 API 잘못된 요청");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new IllegalStateException("카카오 API 외부 서비스 호출 오류");
                })
                .body(LocalAddressResponse.class);

        if (res == null || res.getDocuments() == null || res.getDocuments().isEmpty()) {
            throw new DomainException("ADDRESS_NOT_FOUND",
                    "카카오 주소 정보를 확인할 수 없습니다: " + address, HttpStatus.NOT_FOUND);
        }

        var addr = Optional.ofNullable(res.getDocuments().get(0).getAddress())
                .orElseThrow(() -> new DomainException("ADDRESS_NOT_FOUND",
                        "카카오 주소 상세 정보가 비었습니다: " + address, HttpStatus.NOT_FOUND));

        return new AddressInfo(
                addr.getRegion1depthName(),
                addr.getRegion2depthName(),
                addr.getRegion3depthName()
        );
    }

    private String uriAddParams(String address) {
        return UriComponentsBuilder.fromPath(API_URI)
                .queryParam("query", address)
                .queryParam("page", 1)
                .queryParam("size", 1)
                .build()
                .toUriString();
    }

    @Getter
    public static class LocalAddressResponse {
        @JsonProperty
        private List<Document> documents;

        @Getter
        public static class Document {
            @JsonProperty(value = "address")
            private Address address;

            @Getter
            public static class Address {
                @JsonProperty(value = "region_1depth_name")
                private String region1depthName;
                @JsonProperty(value = "region_2depth_name")
                private String region2depthName;
                @JsonProperty(value = "region_3depth_name")
                private String region3depthName;
            }
        }
    }
}
