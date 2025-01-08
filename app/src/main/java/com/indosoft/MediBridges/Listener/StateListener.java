package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.CardListResponse;
import com.indosoft.MediBridges.Model.IndiaStateResponse;

import java.util.List;

public interface StateListener {

    void onSuccess(List<IndiaStateResponse> response);
    void onError(String error);
}
