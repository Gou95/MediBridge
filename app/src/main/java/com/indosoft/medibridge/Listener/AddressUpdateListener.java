package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.AddressUpdateResponse;

public interface AddressUpdateListener {
    void onSuccess(AddressUpdateResponse response);
    void onError(String error);
}
