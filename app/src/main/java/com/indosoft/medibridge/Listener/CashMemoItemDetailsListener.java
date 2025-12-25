package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.CashMemoItemDetailsResponse;

import java.util.List;

public interface CashMemoItemDetailsListener {
    void onSuccess(List<CashMemoItemDetailsResponse> responses);
    void onError(String error);
}
