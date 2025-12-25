package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.CashMemodetailsResponse;

import java.util.List;

public interface CashMemoDetailsListener {
    void onSuccess(List<CashMemodetailsResponse>responses);
    void onError(String error);
}
