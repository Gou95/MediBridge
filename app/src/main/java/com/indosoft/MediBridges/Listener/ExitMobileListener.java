package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.ExitMobileResponse;

public interface ExitMobileListener {
    void onSuccess(ExitMobileResponse response);
    void onError(String error);
}
