package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.StockitsResponse;
import com.indosoft.medibridge.Model.TotalSalesResponse;

import java.util.List;

public interface TotalSalesListener {
    void onSuccess(List<TotalSalesResponse> response);
    void onError(String error);
}
