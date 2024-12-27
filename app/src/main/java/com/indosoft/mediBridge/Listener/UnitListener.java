package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.UnitResponse;

import java.util.List;

public interface UnitListener {

    void onSuccess(List<UnitResponse> response);
    void onError(String error);
}

