package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.OrderResponse;

public interface OrderListener {
    void onSuccess(OrderResponse response);
    void onError(String error);
}
