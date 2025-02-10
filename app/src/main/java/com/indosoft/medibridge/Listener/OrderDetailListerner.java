package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.OrderDetailsResponse;

import java.util.List;

public interface OrderDetailListerner {
    void onSuccess(List<OrderDetailsResponse> response);
    void onError(String error);

}
