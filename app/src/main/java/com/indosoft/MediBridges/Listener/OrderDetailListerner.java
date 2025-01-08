package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.OrderDetailsResponse;

import java.util.List;

public interface OrderDetailListerner {
    void onSuccess(List<OrderDetailsResponse> response);
    void onError(String error);

}
