package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.OtpResponse;

public interface OtpListener {
    void onSuccess(OtpResponse response);
    void onError(String error);
}
