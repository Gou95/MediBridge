package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.UnlistedItemResponse;

public interface UnlistedItemListener {
    void onSuccess(UnlistedItemResponse response);
    void onError(String error);
}
