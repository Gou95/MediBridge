package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.CardListResponse;

import java.util.List;

public interface CartListener {

    void onSuccess(List<CardListResponse> response);
    void onError(String error);
}
