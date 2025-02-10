package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.ExitMobileResponse;

public interface ExitMobileListener {
    void onSuccess(ExitMobileResponse response);
    void onError(String error);
}
