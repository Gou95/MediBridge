package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.LoginResponse;
import com.indosoft.MediBridges.Model.UserUpdateResponse;

public interface UserUpdateListener {
    void onSuccess(UserUpdateResponse response);
    void onError(String error);
}
