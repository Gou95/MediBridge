package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.LoginResponse;

import java.util.List;

public interface LoginListener {
    void onSuccess(List<LoginResponse> response);
    void onError(String error);
}
