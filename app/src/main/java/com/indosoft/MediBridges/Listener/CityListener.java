package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.IndiaStateResponse;
import com.indosoft.MediBridges.Model.StateCityResponse;

import java.util.List;

public interface CityListener {
    void onSuccess(List<StateCityResponse> response);
    void onError(String error);
}
