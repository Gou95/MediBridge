package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.DealersResponse;
import com.indosoft.MediBridges.Model.MedicineListResponse;

import java.util.List;

public interface MedicineListListener {
    void onSuccess(List<MedicineListResponse> response);
    void onError(String error);
}
