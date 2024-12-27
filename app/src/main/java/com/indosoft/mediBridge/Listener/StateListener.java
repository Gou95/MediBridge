package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.IndiaStateResponse;

import java.util.List;

public interface StateListener {

    void onSuccess(List<IndiaStateResponse> response);
    void onError(String error);
}
