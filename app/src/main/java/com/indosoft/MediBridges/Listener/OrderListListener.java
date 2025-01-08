package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.OrderListResponse;

import java.util.List;

public interface OrderListListener {
    void onSuccess(List<OrderListResponse> response);
    void onError(String error);
}
