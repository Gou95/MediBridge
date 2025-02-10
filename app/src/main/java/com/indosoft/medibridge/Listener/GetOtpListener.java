package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.GetOtpResponse;

public interface GetOtpListener {
    void onSuccess(GetOtpResponse response);
    void onError(String error);
}
