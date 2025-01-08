package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.CardListResponse;
import com.indosoft.MediBridges.Model.DealersResponse;

import java.util.List;

public interface DealerListener {
    void onSuccess(List<DealersResponse> response);
    void onError(String error);
}
