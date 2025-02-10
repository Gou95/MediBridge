package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.UnitResponse;

import java.util.List;

public interface UnitListener {

    void onSuccess(List<UnitResponse> response);
    void onError(String error);
}

