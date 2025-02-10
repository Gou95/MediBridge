package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.SignUpResponse;

public interface SignUpListener {
    void onSuccess(SignUpResponse response);
    void onError(String error);
}
