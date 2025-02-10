package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.OrderResponse;

public interface OrderListener {
    void onSuccess(OrderResponse response);
    void onError(String error);
}
