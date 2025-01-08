package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.ProceedOrderResponse;

public interface ProceedOrderListener {
    void onSuccess(ProceedOrderResponse response);
    void onError(String error);
}
