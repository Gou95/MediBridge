package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.CardListResponse;
import com.indosoft.MediBridges.Model.UnitResponse;

import java.util.List;

public interface UnitListener {

    void onSuccess(List<UnitResponse> response);
    void onError(String error);
}

