package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.GetUrgentCartResponse;

import java.util.List;

public interface GetUrgentCartListener {
    void onSuccess(List<GetUrgentCartResponse> responses);
    void onError(String error);
}
