package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.ShowCartResponse;

import java.util.List;

public interface ShowCartListener {
    void onSuccess(List<ShowCartResponse> response);
    void onError(String error);
}
