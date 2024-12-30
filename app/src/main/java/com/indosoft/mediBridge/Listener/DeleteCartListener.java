package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.DeleteCartResponse;

public interface DeleteCartListener {
    void onSuccess(DeleteCartResponse response);
    void onError(String error);
}
