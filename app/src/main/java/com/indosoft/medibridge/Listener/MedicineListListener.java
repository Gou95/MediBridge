package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.MedicineListResponse;

import java.util.List;

public interface MedicineListListener {
    void onSuccess(List<MedicineListResponse> response);
    void onError(String error);
}
