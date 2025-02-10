package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.LastStockitsResponse;

import java.util.List;

public interface LastStockitsListener {
    void onSuccess(List<LastStockitsResponse> response);
    void onError(String error);
}
