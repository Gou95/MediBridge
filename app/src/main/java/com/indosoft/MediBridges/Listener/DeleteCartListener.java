package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.DeleteCartResponse;

public interface DeleteCartListener {
    void onSuccess(DeleteCartResponse response);
    void onError(String error);
}
