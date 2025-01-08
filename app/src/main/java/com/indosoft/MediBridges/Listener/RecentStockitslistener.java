package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.RecentStockitsResponse;

import java.util.List;

public interface RecentStockitslistener {
    void onSuccess(List<RecentStockitsResponse> responses);
    void onError(String error);
}
