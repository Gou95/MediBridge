package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.CityDealerResponse;

import java.util.List;

public interface CityDealerListener {
    void onSuccess(List<CityDealerResponse> responses);
    void onError(String error);
}
