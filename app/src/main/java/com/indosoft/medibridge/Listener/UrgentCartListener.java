package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.UrgentCartResponse;

public interface UrgentCartListener {
    void onSuccess(UrgentCartResponse response);
    void onError(String error);
}
