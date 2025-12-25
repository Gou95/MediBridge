package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.CashMemoListResponse;

import java.util.List;

public interface CashMemoListListener {
     void onSuccess(List<CashMemoListResponse> responses);
     void onError(String error);
}
