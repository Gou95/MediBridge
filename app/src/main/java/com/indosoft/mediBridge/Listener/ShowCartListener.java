package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.ShowCartResponse;

import java.util.List;

public interface ShowCartListener {
    void onSuccess(List<ShowCartResponse> response);
    void onError(String error);
}
