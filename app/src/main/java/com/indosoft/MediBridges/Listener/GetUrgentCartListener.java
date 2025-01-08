package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.GetUrgentCartResponse;

import java.util.List;

public interface GetUrgentCartListener {
    void onSuccess(List<GetUrgentCartResponse> responses);
    void onError(String error);
}
