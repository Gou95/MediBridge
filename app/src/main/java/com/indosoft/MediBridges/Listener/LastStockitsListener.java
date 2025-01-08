package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.DealersResponse;
import com.indosoft.MediBridges.Model.LastStockitsResponse;

import java.util.List;

public interface LastStockitsListener {
    void onSuccess(List<LastStockitsResponse> response);
    void onError(String error);
}
