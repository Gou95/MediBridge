package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.AddtoCartResponse;

public interface AddtoCartListener {
    void onSuccess(AddtoCartResponse response);
    void onError(String error);
}
