package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.CityDealerResponse;

import java.util.List;

public interface CityDealerListener {
    void onSuccess(List<CityDealerResponse> responses);
    void onError(String error);
}
