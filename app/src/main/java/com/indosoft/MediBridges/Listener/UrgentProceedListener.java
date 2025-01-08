package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.UrgentProceedResponse;

public interface UrgentProceedListener {
    void onSuccess(UrgentProceedResponse response);
    void onError(String error);
}
