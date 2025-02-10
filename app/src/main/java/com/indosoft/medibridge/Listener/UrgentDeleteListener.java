package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.UrgentDeleteResponse;

public interface UrgentDeleteListener {

    void onSuccess(UrgentDeleteResponse response);
    void onError(String error);
}
