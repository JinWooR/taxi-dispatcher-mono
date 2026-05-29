package com.taxidispatcher.services.dispatcher.infrastructure.external;

public interface AddressInfoSearcher {

    AddressInfo search(String address);

    record AddressInfo(
            String sd,
            String sgg,
            String emd
    ) {
    }
}
