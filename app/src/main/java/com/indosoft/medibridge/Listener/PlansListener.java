package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.PlansResponse;

import java.util.List;

public interface PlansListener {
    void onSuccess(List<PlansResponse> response);
    void onError(String error);
}
