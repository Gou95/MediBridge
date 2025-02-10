package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.LastOrderResponse;

import java.util.List;

public interface LastOrderListener {
    void onSuccess (List<LastOrderResponse> responses);
    void onError(String error);
}
