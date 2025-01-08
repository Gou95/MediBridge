package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.LastOrderResponse;

import java.util.List;

public interface LastOrderListener {
    void onSuccess (List<LastOrderResponse> responses);
    void onError(String error);
}
