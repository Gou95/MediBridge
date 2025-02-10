package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.IndiaStateResponse;

import java.util.List;

public interface StateListener {

    void onSuccess(List<IndiaStateResponse> response);
    void onError(String error);
}
