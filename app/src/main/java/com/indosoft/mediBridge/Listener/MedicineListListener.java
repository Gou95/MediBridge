package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.DealersResponse;
import com.indosoft.mediBridge.Model.MedicineListResponse;

import java.util.List;

public interface MedicineListListener {
    void onSuccess(List<MedicineListResponse> response);
    void onError(String error);
}
