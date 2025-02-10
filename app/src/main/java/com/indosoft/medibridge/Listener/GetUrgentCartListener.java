package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.GetUrgentCartResponse;

import java.util.List;

public interface GetUrgentCartListener {
    void onSuccess(List<GetUrgentCartResponse> responses);
    void onError(String error);
}
