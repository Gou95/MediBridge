package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.UserUpdateResponse;

public interface UserUpdateListener {
    void onSuccess(UserUpdateResponse response);
    void onError(String error);
}
