package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.StateCityResponse;

import java.util.List;

public interface CityListener {
    void onSuccess(List<StateCityResponse> response);
    void onError(String error);
}
