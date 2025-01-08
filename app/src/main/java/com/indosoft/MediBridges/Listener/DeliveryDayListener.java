package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.DeliveryDayResponse;

public interface DeliveryDayListener {
    void onSuccess(DeliveryDayResponse response);
    void onError(String error);
}
