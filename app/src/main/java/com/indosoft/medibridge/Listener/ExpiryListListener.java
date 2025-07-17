package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.ExpiryListResponse;

import java.util.List;

public interface ExpiryListListener {
    void onSuccess(List<ExpiryListResponse> response);
    void onError(String error);
}
