package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.CardListResponse;

import java.util.List;

public interface CartListener {

    void onSuccess(List<CardListResponse> response);
    void onError(String error);
}
