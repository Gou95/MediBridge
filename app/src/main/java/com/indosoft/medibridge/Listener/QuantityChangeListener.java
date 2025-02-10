package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.QuantityChangeResponse;

public interface QuantityChangeListener {
    void onSuccess(QuantityChangeResponse response);
    void onError(String error);
}
