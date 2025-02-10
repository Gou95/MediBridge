package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.DealersResponse;

import java.util.List;

public interface DealerListener {
    void onSuccess(List<DealersResponse> response);
    void onError(String error);
}
