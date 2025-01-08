package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.UrgentDeleteResponse;

public interface UrgentDeleteListener {

    void onSuccess(UrgentDeleteResponse response);
    void onError(String error);
}
