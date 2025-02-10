package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.DeleteCartResponse;

public interface DeleteCartListener {
    void onSuccess(DeleteCartResponse response);
    void onError(String error);
}
