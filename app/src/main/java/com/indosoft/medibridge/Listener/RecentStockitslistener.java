package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.RecentStockitsResponse;

import java.util.List;

public interface RecentStockitslistener {
    void onSuccess(List<RecentStockitsResponse> responses);
    void onError(String error);
}
