package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.OrderListResponse;

import java.util.List;

public interface OrderListListener {
    void onSuccess(List<OrderListResponse> response);
    void onError(String error);
}
