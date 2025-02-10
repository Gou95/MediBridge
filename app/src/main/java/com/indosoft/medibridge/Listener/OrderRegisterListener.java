package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.OrderRegisterResponse;

import java.util.List;

public interface OrderRegisterListener {
    void onSuccess(List<OrderRegisterResponse> response);
    void onError(String error);
}
