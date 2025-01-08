package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.StockitsResponse;

import java.util.List;

public interface StockitsListener {
    void onSuccess(List<StockitsResponse> response);
    void onError(String error);
}
