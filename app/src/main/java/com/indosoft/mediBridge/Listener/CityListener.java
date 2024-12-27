package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.IndiaStateResponse;
import com.indosoft.mediBridge.Model.StateCityResponse;

import java.util.List;

public interface CityListener {
    void onSuccess(List<StateCityResponse> response);
    void onError(String error);
}
