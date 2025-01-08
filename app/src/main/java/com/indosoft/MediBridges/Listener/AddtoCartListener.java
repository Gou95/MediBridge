package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.AddtoCartResponse;

public interface AddtoCartListener {
    void onSuccess(AddtoCartResponse response);
    void onError(String error);
}
