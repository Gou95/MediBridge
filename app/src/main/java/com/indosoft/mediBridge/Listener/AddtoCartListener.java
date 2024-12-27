package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.AddtoCartResponse;

public interface AddtoCartListener {
    void onSuccess(AddtoCartResponse response);
    void onError(String error);
}
