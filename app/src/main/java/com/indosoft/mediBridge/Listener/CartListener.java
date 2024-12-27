package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.CardListResponse;

import java.util.List;

public interface CartListener {

    void onSuccess(List<CardListResponse> response);
    void onError(String error);
}
