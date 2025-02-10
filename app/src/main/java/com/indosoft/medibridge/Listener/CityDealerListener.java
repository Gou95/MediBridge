package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.CityDealerResponse;

import java.util.List;

public interface CityDealerListener {
    void onSuccess(List<CityDealerResponse> responses);
    void onError(String error);
}
