package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.StockitsResponse;

import java.util.List;

public interface StockitsListener {
    void onSuccess(List<StockitsResponse> response);
    void onError(String error);
}
