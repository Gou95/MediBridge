package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.DealersResponse;

import java.util.List;

public interface DealerListener {
    void onSuccess(List<DealersResponse> response);
    void onError(String error);
}
