package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.DealersResponse;
import com.indosoft.MediBridges.Model.LoginResponse;

import java.util.List;

public interface LoginListener {
    void onSuccess(List<LoginResponse> response);
    void onError(String error);
}
