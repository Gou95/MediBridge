package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.UrgentProceedResponse;

public interface UrgentProceedListener {
    void onSuccess(UrgentProceedResponse response);
    void onError(String error);
}
