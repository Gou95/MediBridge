package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.UrgentCartResponse;

public interface UrgentCartListener {
    void onSuccess(UrgentCartResponse response);
    void onError(String error);
}
