package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.UrgentCartResponse;

public interface UrgentCartListener {
    void onSuccess(UrgentCartResponse response);
    void onError(String error);
}
