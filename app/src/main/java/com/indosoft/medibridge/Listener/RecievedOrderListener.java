package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.RecievedOrderResponse;

import java.util.List;

public interface RecievedOrderListener {
    void onSuccess(List<RecievedOrderResponse> responses);
    void onError(String error);
}
