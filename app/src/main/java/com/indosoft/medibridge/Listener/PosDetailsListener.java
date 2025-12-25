package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.PosDetailsResponse;

import java.util.List;

public interface PosDetailsListener {
    void onSuccess(List<PosDetailsResponse> response);
    void onError(String error);
}
