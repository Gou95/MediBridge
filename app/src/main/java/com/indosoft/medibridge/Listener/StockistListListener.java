package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.StockistListResponse;

import java.util.List;

public interface StockistListListener {
    void onSuccess(List<StockistListResponse> responses);
    void onError(String error);
}
