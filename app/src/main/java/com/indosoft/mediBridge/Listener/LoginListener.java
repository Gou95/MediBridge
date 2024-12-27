package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.DealersResponse;
import com.indosoft.mediBridge.Model.LoginResponse;

import java.util.List;

public interface LoginListener {
    void onSuccess(List<LoginResponse> response);
    void onError(String error);
}
