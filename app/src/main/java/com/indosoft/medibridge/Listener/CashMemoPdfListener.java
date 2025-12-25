package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.CashMemoPdfResponse;

import java.util.List;

public interface CashMemoPdfListener {
    void onSuccess(List<CashMemoPdfResponse> responses);
    void onError(String error);
}
