package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.DeliveryDayResponse;

public interface DeliveryDayListener {
    void onSuccess(DeliveryDayResponse response);
    void onError(String error);
}
