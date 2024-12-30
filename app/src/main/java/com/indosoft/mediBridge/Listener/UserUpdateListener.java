package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.LoginResponse;
import com.indosoft.mediBridge.Model.UserUpdateResponse;

import java.util.List;

public interface UserUpdateListener {
    void onSuccess(UserUpdateResponse response);
    void onError(String error);
}
