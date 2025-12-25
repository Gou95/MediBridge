package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.PosAllListResponse;

import java.util.List;

public interface PosAllListListener {
    void onSuccess(List<PosAllListResponse> responses);
    void onError(String error);
}
