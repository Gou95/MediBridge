package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.ShowCartResponse;

import java.util.List;

public interface ShowCartListener {
    void onSuccess(List<ShowCartResponse> response);
    void onError(String error);
}
