package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.QuantityChangeResponse;

public interface QuantityChangeListener {
    void onSuccess(QuantityChangeResponse response);
    void onError(String error);
}
