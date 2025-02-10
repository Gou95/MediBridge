package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.CounterResponse;

public interface CounterListener {
    void onSuccess (CounterResponse response);
    void onError(String error);
}
