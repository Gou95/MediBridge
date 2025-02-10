package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.ProceedOrderResponse;

public interface ProceedOrderListener {
    void onSuccess(ProceedOrderResponse response);
    void onError(String error);
}
