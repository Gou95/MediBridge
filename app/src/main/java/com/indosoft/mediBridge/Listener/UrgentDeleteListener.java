package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.UrgentDeleteResponse;

public interface UrgentDeleteListener {

    void onSuccess(UrgentDeleteResponse response);
    void onError(String error);
}
