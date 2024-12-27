package com.indosoft.GoodBooks.Listener;

import com.indosoft.GoodBooks.Model.CardListResponse;

import java.util.List;

public interface CartListener {

    void onSuccess(List<CardListResponse> response);
    void onError(String error);
}
